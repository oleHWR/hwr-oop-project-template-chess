package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UndoTest {

	@Test
	fun `undo restores the previous board and turn`() {
		val game = Game(GameID("g"))

		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
		val reverted = next.undo()

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
	fun `undo restores the previous position status and halfmove clock`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Knight(Color.WHITE, Square(File.B, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE), halfmoveClock = 10)

		val next = game.makeMove(Move(Square(File.B, 1), Square(File.C, 3)))
		val reverted = next.undo()

		assertThat(reverted.halfmoveClock).isEqualTo(10)
		assertThat(reverted.positionStatus).isEqualTo(PositionStatus.NORMAL)
	}

	@Test
	fun `undo restores en passant target from before the move`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		val next = game.makeMove(Move(Square(File.A, 7), Square(File.A, 5)))
		val reverted = next.undo()

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
	fun `undo can go back after a checkmate`() {
		val board = Board()
		board.place(King(Color.BLACK, Square(File.H, 8)))
		board.place(Pawn(Color.BLACK, Square(File.F, 7)))
		board.place(Pawn(Color.BLACK, Square(File.G, 7)))
		board.place(Pawn(Color.BLACK, Square(File.H, 7)))
		board.place(Rook(Color.WHITE, Square(File.D, 1)))
		board.place(King(Color.WHITE, Square(File.A, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val mated = game.makeMove(Move(Square(File.D, 1), Square(File.D, 8)))
		val reverted = mated.undo()

		assertThat(reverted.status).isEqualTo(GameStatus.ONGOING)
		assertThat(reverted.turn.color).isEqualTo(Color.WHITE)
		assertThat(reverted.board.pieceAt(Square(File.D, 1))?.type).isEqualTo(PieceType.ROOK)
		assertThat(reverted.board.pieceAt(Square(File.D, 8))).isNull()
	}

	@Test
	fun `undo after castling restores king and rook to their starting squares`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Rook(Color.WHITE, Square(File.H, 1)))
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val castled = game.makeMove(Move(Square(File.E, 1), Square(File.G, 1)))
		val reverted = castled.undo()

		assertThat(reverted.board.pieceAt(Square(File.E, 1)))
			.isEqualTo(King(Color.WHITE, Square(File.E, 1)))
		assertThat(reverted.board.pieceAt(Square(File.H, 1)))
			.isEqualTo(Rook(Color.WHITE, Square(File.H, 1)))
		assertThat(reverted.board.pieceAt(Square(File.G, 1))).isNull()
		assertThat(reverted.board.pieceAt(Square(File.F, 1))).isNull()
	}

	@Test
	fun `undo after en passant restores both pawns`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 5)))
		board.place(Pawn(Color.BLACK, Square(File.D, 7)))
		val game = Game(GameID("g"), board, Turn(1, Color.BLACK))
			.makeMove(Move(Square(File.D, 7), Square(File.D, 5)))

		val captured = game.makeMove(Move(Square(File.E, 5), Square(File.D, 6)))
		val reverted = captured.undo()

		assertThat(reverted.board.pieceAt(Square(File.E, 5)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 5)))
		assertThat(reverted.board.pieceAt(Square(File.D, 5)))
			.isEqualTo(Pawn(Color.BLACK, Square(File.D, 5), hasMoved = true))
		assertThat(reverted.board.pieceAt(Square(File.D, 6))).isNull()
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
}
