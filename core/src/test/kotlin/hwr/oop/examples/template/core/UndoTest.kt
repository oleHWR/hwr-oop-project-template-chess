package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UndoTest {

	@Test
	fun `undo restores the previous board and turn`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		val reverted = game.undo()

		assertThat(reverted.turn.color).isEqualTo(Color.WHITE)
		assertThat(reverted.turn.number).isEqualTo(1)
		assertThat(reverted.board.pieceAt(Square(File.E, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 2)))
		assertThat(reverted.board.pieceAt(Square(File.E, 4))).isNull()
	}

	@Test
	fun `undo throws when there is no previous state`() {
		val game = Game(GameID("g"))

		assertThatThrownBy { game.undo() }
			.isInstanceOf(IllegalStateException::class.java)
			.hasMessage("No move to undo")
	}

	@Test
	fun `undo trims the halfmove clock and en passant target back`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.A, 7), Square(File.A, 5)))

		val reverted = game.undo()

		assertThat(reverted.enPassantTarget).isEqualTo(Square(File.E, 3))
	}

	@Test
	fun `undo chain supports multiple steps back`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))

		val start = game.undo().undo()

		assertThat(start.turn.color).isEqualTo(Color.WHITE)
		assertThat(start.turn.number).isEqualTo(1)
		assertThat(start.board.pieceAt(Square(File.E, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 2)))
		assertThat(start.board.pieceAt(Square(File.E, 7)))
			.isEqualTo(Pawn(Color.BLACK, Square(File.E, 7)))
	}

	@Test
	fun `making a move after undo works as expected`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.undo()

		val next = game.makeMove(Move(Square(File.D, 2), Square(File.D, 4)))

		assertThat(next.board.pieceAt(Square(File.D, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.D, 4), hasMoved = true))
		assertThat(next.turn.color).isEqualTo(Color.BLACK)
	}

	@Test
	fun `undo after castling restores king and rook to their starting squares`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.G, 1), Square(File.F, 3)))
			.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))
			.makeMove(Move(Square(File.G, 2), Square(File.G, 3)))
			.makeMove(Move(Square(File.G, 7), Square(File.G, 6)))
			.makeMove(Move(Square(File.F, 1), Square(File.G, 2)))
			.makeMove(Move(Square(File.F, 8), Square(File.G, 7)))
			.makeMove(Move(Square(File.E, 1), Square(File.G, 1)))

		val reverted = game.undo()

		assertThat(reverted.board.pieceAt(Square(File.E, 1)))
			.isEqualTo(King(Color.WHITE, Square(File.E, 1)))
		assertThat(reverted.board.pieceAt(Square(File.H, 1)))
			.isEqualTo(Rook(Color.WHITE, Square(File.H, 1)))
	}
}
