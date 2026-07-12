package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.parse
import hwr.oop.examples.template.FileSystemPersistence
import hwr.oop.examples.template.FileSystemPersistenceConfiguration
import hwr.oop.examples.template.core.Board
import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.GameStatus
import hwr.oop.examples.template.core.King
import hwr.oop.examples.template.core.Pawn
import hwr.oop.examples.template.core.Queen
import hwr.oop.examples.template.core.Square
import hwr.oop.examples.template.core.Turn
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CliFileSystemTest {
	
	private val fakeFileSystem = FakeFileSystem()
	private val tempDir = "/tmp/cli-fs-test".toPath()
	private lateinit var persistence: FileSystemPersistence
	
	@BeforeEach
	fun setUp() {
		fakeFileSystem.createDirectories(tempDir)
		persistence = FileSystemPersistence(
			FileSystemPersistenceConfiguration(tempDir),
			fakeFileSystem
		)
	}
	
	@AfterEach
	fun tearDown() {
		fakeFileSystem.checkNoOpenFiles()
	}
	
	@Test
	fun `start game stores a new game`() {
		// given
		val args = listOf(
			"startGame",
			"--game-id", "game-1",
			"--white-player-id", "alice",
			"--black-player-id", "bob",
		)

		// when
		buildCli(persistence).parse(args)

		// then
		val game = persistence.loadById(GameID("game-1"))
		assertThat(game.id.value).isEqualTo("game-1")
		assertThat(game.whitePlayerId).isEqualTo("alice")
		assertThat(game.blackPlayerId).isEqualTo("bob")
		assertThat(game.turn.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `make move loads updates and stores the game`() {
		// given
		startGame()

		// when
		buildCli(persistence).parse(
			listOf(
				"onGameID", "game-1",
				"makeMove",
				"--player-id", "alice",
				"--from", "E2",
				"--to", "E4",
			)
		)

		// then
		val game = persistence.loadById(GameID("game-1"))
		assertThat(game.turn.color).isEqualTo(Color.BLACK)
		assertThat(game.board.pieceAt(Square(File.E, 2))).isNull()
		assertThat(game.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}

	@Test
	fun `make move supports pawn promotion and stores the promoted piece`() {
		// given
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.A, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 7), hasMoved = true))
		persistence.save(
			Game(
				id = GameID("promotion-game"),
				board = board,
				turn = Turn(1, Color.WHITE),
				whitePlayerId = "alice",
				blackPlayerId = "bob",
			)
		)

		// when
		buildCli(persistence).parse(
			listOf(
				"onGameID", "promotion-game",
				"makeMove",
				"--player-id", "alice",
				"--from", "E7",
				"--to", "E8",
				"--promotion-piece", "QUEEN",
			)
		)

		// then
		val game = persistence.loadById(GameID("promotion-game"))
		assertThat(game.board.pieceAt(Square(File.E, 8)))
			.isEqualTo(Queen(Color.WHITE, Square(File.E, 8), hasMoved = true))
		assertThat(game.board.pieceAt(Square(File.E, 7))).isNull()
	}

	@Test
	fun `draw offer and acceptance update the stored game`() {
		// given
		startGame()

		// when
		buildCli(persistence).parse(
			listOf(
				"onGameID", "game-1",
				"offerDraw",
				"--player-id", "alice",
			)
		)
		buildCli(persistence).parse(
			listOf(
				"onGameID", "game-1",
				"acceptDraw",
				"--player-id", "bob",
			)
		)

		// then
		val game = persistence.loadById(GameID("game-1"))
		assertThat(game.status).isEqualTo(GameStatus.FINISHED)
		assertThat(game.result?.winner).isNull()
		assertThat(game.pendingDrawOfferBy).isNull()
	}

	@Test
	fun `get available moves can read a stored game`() {
		// given
		startGame()

		// when / then
		buildCli(persistence).parse(listOf("onGameID", "game-1", "getAvailableMoves"))
	}

	@Test
	fun `undo reverts the most recent move`() {
		startGame()
		buildCli(persistence).parse(
			listOf(
				"onGameID", "game-1",
				"makeMove",
				"--player-id", "alice",
				"--from", "E2",
				"--to", "E4",
			)
		)

		buildCli(persistence).parse(listOf("onGameID", "game-1", "undo"))

		val game = persistence.loadById(GameID("game-1"))
		assertThat(game.turn.color).isEqualTo(Color.WHITE)
		assertThat(game.moveHistory).isEmpty()
		assertThat(game.board.pieceAt(Square(File.E, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 2)))
		assertThat(game.board.pieceAt(Square(File.E, 4))).isNull()
	}

	private fun startGame() {
		buildCli(persistence).parse(
			listOf(
				"startGame",
				"--game-id", "game-1",
				"--white-player-id", "alice",
				"--black-player-id", "bob",
			)
		)
	}
	
}
