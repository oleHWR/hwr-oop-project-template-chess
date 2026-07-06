package hwr.oop.examples.template

import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.Move
import hwr.oop.examples.template.core.Pawn
import hwr.oop.examples.template.core.Square
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class FileSystemPersistenceTest {
	
	private val fakeFileSystem = FakeFileSystem()
	private val tempDir = "/tmp/template-test".toPath()
	private val sut: FileSystemPersistence
	
	init {
		fakeFileSystem.createDirectories(tempDir)
		sut = FileSystemPersistence(
			FileSystemPersistenceConfiguration(tempDir),
			fakeFileSystem
		)
	}
	
	@AfterEach
	fun tearDown() {
		fakeFileSystem.checkNoOpenFiles()
	}
	
	@Test
	fun `can store and load games in file system`() {
		// given
		val game = Game(GameID("game-1"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		// when
		sut.save(game)
		val loaded = sut.loadById(GameID("game-1"))

		// then
		assertThat(loaded.id.value).isEqualTo("game-1")
		assertThat(loaded.turn.color).isEqualTo(Color.BLACK)
		assertThat(loaded.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}

	@Test
	fun `load game not saved, exception`() {
		// when / then
		assertThatThrownBy {
			sut.loadById(GameID("missing-game"))
		}.hasMessageContainingAll("Could not load game", "missing-game")
	}
	
}

