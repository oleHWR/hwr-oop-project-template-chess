package hwr.oop.examples.template.service

import hwr.oop.examples.template.FileSystemPersistence
import hwr.oop.examples.template.FileSystemPersistenceConfiguration
import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(
	webEnvironment = MOCK,
	properties = ["spring.main.allow-bean-definition-overriding=true"]
)
class ServiceFileSystemTest {

	@TestConfiguration
	class Config {
		private val fakeFileSystem = FakeFileSystem()
		private val tempDir = "/tmp/service-fs-test".toPath()
		private val persistence: FileSystemPersistence = FileSystemPersistence(
			FileSystemPersistenceConfiguration(tempDir),
			fakeFileSystem.also { it.createDirectories(tempDir) }
		)

		@Bean
		@Primary
		fun persistence(): GameRepository = persistence
	}

	@Autowired
	private lateinit var webApplicationContext: WebApplicationContext

	@Autowired
	private lateinit var persistence: GameRepository

	private lateinit var mockMvc: MockMvc

	@BeforeEach
	fun setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
	}

	@Test
	fun `start game creates a new game and returns its id`() {
		mockMvc.perform(
			post("/games")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"whitePlayerId":"alice","blackPlayerId":"bob"}""")
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.gameId").isNotEmpty)
	}

	@Test
	fun `full move flow works over http`() {
		val gameId = createGame()

		mockMvc.perform(
			post("/games/$gameId/moves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"""{"playerId":"alice","from":{"file":"E","rank":2},"to":{"file":"E","rank":4}}"""
				)
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
			.andExpect(jsonPath("$.currentTurn").value("BLACK"))

		mockMvc.perform(get("/games/$gameId"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.gameId").value(gameId))
	}

	@Test
	fun `move endpoint rejects a player who is not on turn`() {
		val gameId = createGame()

		mockMvc.perform(
			post("/games/$gameId/moves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"""{"playerId":"bob","from":{"file":"E","rank":2},"to":{"file":"E","rank":4}}"""
				)
		)
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("Bad Request"))

		val stored = persistence.loadById(GameID(gameId))
		assertThat(stored.turn.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `draw response endpoint rejects the player who made the offer`() {
		val gameId = createGame()
		mockMvc.perform(
			post("/games/$gameId/draw-offer")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"playerId":"alice"}""")
		).andExpect(status().isOk)

		mockMvc.perform(
			post("/games/$gameId/draw-offer/acceptance")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"playerId":"alice"}""")
		)
			.andExpect(status().isBadRequest)

		val stored = persistence.loadById(GameID(gameId))
		assertThat(stored.pendingDrawOfferBy).isEqualTo(Color.WHITE)
	}

	@Test
	fun `available moves endpoint returns 20 moves for a new game`() {
		val gameId = createGame()

		mockMvc.perform(get("/games/$gameId/moves"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.moves.length()").value(20))
	}

	@Test
	fun `resign endpoint finishes the game`() {
		val gameId = createGame()

		mockMvc.perform(
			post("/games/$gameId/resignation")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"playerId":"alice"}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.status").value("RESIGNED"))
	}

	@Test
	fun `missing game returns not found`() {
		mockMvc.perform(get("/games/does-not-exist"))
			.andExpect(status().isNotFound)
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.error").value("Not Found"))
	}

	@Test
	fun `action on finished game returns conflict`() {
		val gameId = createGame()
		mockMvc.perform(
			post("/games/$gameId/resignation")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"playerId":"alice"}""")
		).andExpect(status().isOk)

		mockMvc.perform(
			post("/games/$gameId/moves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"""{"playerId":"alice","from":{"file":"E","rank":2},"to":{"file":"E","rank":4}}"""
				)
		)
			.andExpect(status().isConflict)
			.andExpect(jsonPath("$.status").value(409))
			.andExpect(jsonPath("$.error").value("Conflict"))
	}

	@Test
	fun `malformed request returns bad request`() {
		mockMvc.perform(
			post("/games")
				.contentType(MediaType.APPLICATION_JSON)
				.content("not-json")
		)
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.status").value(400))
	}

	private fun createGame(): String {
		val response = mockMvc.perform(
			post("/games")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"whitePlayerId":"alice","blackPlayerId":"bob"}""")
		).andReturn().response.contentAsString
		return Regex("\"gameId\":\"([^\"]+)\"").find(response)!!.groupValues[1]
	}
}
