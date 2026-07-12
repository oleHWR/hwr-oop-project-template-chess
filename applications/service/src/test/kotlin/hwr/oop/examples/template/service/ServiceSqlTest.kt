package hwr.oop.examples.template.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import hwr.oop.examples.template.SqlPersistence
import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.Pawn
import hwr.oop.examples.template.core.Square
import hwr.oop.examples.template.ports.out.GameRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@EnabledIfEnvironmentVariable(named = "RUN_DOCKER_TESTS", matches = "true")
@Testcontainers
@SpringBootTest(
	webEnvironment = MOCK,
	properties = ["spring.main.allow-bean-definition-overriding=true"]
)
class ServiceSqlTest {
	
	companion object {
		@Container
		@JvmStatic
		val postgres = PostgreSQLContainer("postgres:17-alpine")
	}
	
	@TestConfiguration
	class Config {
		private val persistence = SqlPersistence(
			HikariDataSource(HikariConfig().apply {
				jdbcUrl = postgres.jdbcUrl
				username = postgres.username
				password = postgres.password
			})
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
	fun `start game stores a new game in sql`() {
		// when
		val gameId = createGame("sql-alice", "sql-bob")

		// then
		val game = persistence.loadById(GameID(gameId))
		assertThat(game.whitePlayerId).isEqualTo("sql-alice")
		assertThat(game.blackPlayerId).isEqualTo("sql-bob")
		assertThat(game.turn.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `move endpoint updates a sql stored game`() {
		// given
		val gameId = createGame("alice", "bob")

		// when
		mockMvc.perform(
			post("/games/$gameId/moves")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"""{"playerId":"alice","from":{"file":"E","rank":2},"to":{"file":"E","rank":4}}"""
				)
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.currentTurn").value("BLACK"))

		// then
		val game = persistence.loadById(GameID(gameId))
		assertThat(game.turn.color).isEqualTo(Color.BLACK)
		assertThat(game.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}

	private fun createGame(white: String, black: String): String {
		val response = mockMvc.perform(
			post("/games")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"whitePlayerId":"$white","blackPlayerId":"$black"}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.gameId").isNotEmpty)
			.andReturn()
			.response
			.contentAsString

		return Regex("\"gameId\":\"([^\"]+)\"").find(response)!!.groupValues[1]
	}
	
}

