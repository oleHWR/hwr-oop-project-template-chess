package hwr.oop.examples.template

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.Move
import hwr.oop.examples.template.core.Pawn
import hwr.oop.examples.template.core.Square
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Disabled("Requires Docker")
@Testcontainers
class SqlPersistenceTest {
	
	companion object {
		@Container
		@JvmStatic
		val postgres = PostgreSQLContainer("postgres:17-alpine")
	}
	
	private lateinit var adapter: SqlPersistence
	private lateinit var dataSource: HikariDataSource
	
	@BeforeEach
	fun setUp() {
		val config = HikariConfig().apply {
			jdbcUrl = postgres.jdbcUrl
			username = postgres.username
			password = postgres.password
		}
		dataSource = HikariDataSource(config)
		adapter = SqlPersistence(dataSource)
	}
	
	@AfterEach
	fun tearDown() {
		if (::dataSource.isInitialized) {
			dataSource.close()
		}
	}
	
	@Test
	fun `can store and load games in sql`() {
		// given
		val game = Game(GameID("game-1"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		// when
		adapter.save(game)
		val loaded = adapter.loadById(GameID("game-1"))

		// then
		assertThat(loaded.id.value).isEqualTo("game-1")
		assertThat(loaded.turn.color).isEqualTo(Color.BLACK)
		assertThat(loaded.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}

	@Test
	fun `save replaces existing game in sql`() {
		// given
		val gameId = GameID("game-1")
		adapter.save(Game(gameId))
		val movedGame = Game(gameId)
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		// when
		adapter.save(movedGame)
		val loaded = adapter.loadById(gameId)

		// then
		assertThat(loaded.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}

	@Test
	fun `load game not saved, exception`() {
		// when / then
		assertThatThrownBy {
			adapter.loadById(GameID("missing-game"))
		}.hasMessageContainingAll("Could not load game", "missing-game")
	}
	
}

