package hwr.oop.examples.template

import com.zaxxer.hikari.HikariDataSource
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository
import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import liquibase.Liquibase
import liquibase.Scope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.logging.core.NoOpLogService
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.ui.LoggerUIService
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.sql.DataSource

class SqlPersistence(private val dataSource: DataSource) : GameRepository {
	
	constructor(jdbcUrl: String, username: String, password: String) : this(
		HikariDataSource().apply {
			setJdbcUrl(jdbcUrl)
			setUsername(username)
			setPassword(password)
		}
	)
	
	init {
		runLiquibaseMigrations()
		Database.connect(dataSource)
	}
	
	private fun runLiquibaseMigrations() {
		System.setProperty("liquibase.command.update.showSummary", "OFF")
		val scopeAttrs = mapOf(
			Scope.Attr.logService.name to NoOpLogService(),
			Scope.Attr.ui.name to LoggerUIService(),
		)
		Scope.child(scopeAttrs) {
			dataSource.connection.use { connection ->
				val database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(JdbcConnection(connection))
				Liquibase(
					"db/changelog/db.changelog-master.yaml",
					ClassLoaderResourceAccessor(),
					database
				).update("")
			}
		}
	}

	override fun save(game: Game) {
		transaction {
			ChessGamesTable.deleteWhere { ChessGamesTable.id eq game.id.value }
			ChessGamesTable.insert {
				it[id] = game.id.value
				it[this.game] = game
			}
		}
	}

	override fun loadById(gameId: GameID): Game {
		val result = transaction {
			ChessGamesTable.select(ChessGamesTable.game)
				.where { ChessGamesTable.id eq gameId.value }
				.map { it[ChessGamesTable.game] }
				.firstOrNull()
		}
		return result ?: throw LoadGameByIdPort.CouldNotLoadException(gameId)
	}
	
}

