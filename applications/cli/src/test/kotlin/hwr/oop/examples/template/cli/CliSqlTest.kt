package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.parse
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import hwr.oop.examples.template.SqlPersistence
import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.Pawn
import hwr.oop.examples.template.core.Square
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@EnabledIfEnvironmentVariable(named = "RUN_DOCKER_TESTS", matches = "true")
@Testcontainers
class CliSqlTest {
	
	companion object {
		@Container
		@JvmStatic
		val postgres = PostgreSQLContainer("postgres:17-alpine")
	}
	
	private lateinit var persistence: SqlPersistence
	private lateinit var dataSource: HikariDataSource
	
	@BeforeEach
	fun setUp() {
		val config = HikariConfig().apply {
			jdbcUrl = postgres.jdbcUrl
			username = postgres.username
			password = postgres.password
		}
		dataSource = HikariDataSource(config)
		persistence = SqlPersistence(dataSource)
	}
	
	@AfterEach
	fun tearDown() {
		if (::dataSource.isInitialized) {
			dataSource.close()
		}
	}
	
	@Test
	fun `start game stores a new game in sql`() {
		// given
		val args = listOf(
			"startGame",
			"--game-id", "sql-game-1",
			"--white-player-id", "alice",
			"--black-player-id", "bob",
		)

		// when
		buildCli(persistence).parse(args)

		// then
		val game = persistence.loadById(GameID("sql-game-1"))
		assertThat(game.whitePlayerId).isEqualTo("alice")
		assertThat(game.blackPlayerId).isEqualTo("bob")
		assertThat(game.turn.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `make move updates a sql stored game`() {
		// given
		buildCli(persistence).parse(
			listOf(
				"startGame",
				"--game-id", "sql-game-2",
				"--white-player-id", "alice",
				"--black-player-id", "bob",
			)
		)

		// when
		buildCli(persistence).parse(
			listOf(
				"onGameID", "sql-game-2",
				"makeMove",
				"--player-id", "alice",
				"--from", "E2",
				"--to", "E4",
			)
		)

		// then
		val game = persistence.loadById(GameID("sql-game-2"))
		assertThat(game.turn.color).isEqualTo(Color.BLACK)
		assertThat(game.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}
}
