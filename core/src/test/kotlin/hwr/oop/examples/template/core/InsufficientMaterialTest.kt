package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InsufficientMaterialTest {

	@Test
	fun `game ends when only kings remain`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.A, 2)))
		board.place(Pawn(Color.BLACK, Square(File.B, 3)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.A, 2), Square(File.B, 3)))
			.makeMove(Move(Square(File.E, 8), Square(File.E, 7)))
			.makeMove(Move(Square(File.B, 3), Square(File.B, 4)))

		val boardAfter = next.board
		val remaining = boardAfter.pieces()
		assertThat(remaining).hasSize(3)
		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `king vs king reaches insufficient material`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Knight(Color.WHITE, Square(File.B, 4)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.B, 4), Square(File.C, 6)))

		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.INSUFFICIENT_MATERIAL))
	}

	@Test
	fun `game ends with K plus knight vs K`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Knight(Color.WHITE, Square(File.B, 4)))
		board.place(Pawn(Color.BLACK, Square(File.A, 6)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.B, 4), Square(File.A, 6)))

		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.INSUFFICIENT_MATERIAL))
	}

	@Test
	fun `game ends with K plus bishop vs K`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Bishop(Color.WHITE, Square(File.B, 2)))
		board.place(Pawn(Color.BLACK, Square(File.A, 3)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.B, 2), Square(File.A, 3)))

		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.INSUFFICIENT_MATERIAL))
	}

	@Test
	fun `K plus rook vs K keeps the game ongoing`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.BLACK, Square(File.H, 7)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.A, 1), Square(File.A, 2)))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `K plus queen vs K keeps the game ongoing`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Queen(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.BLACK, Square(File.H, 7)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.A, 1), Square(File.A, 2)))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `two same-square-color bishops is insufficient`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Bishop(Color.WHITE, Square(File.F, 1)))
		board.place(Bishop(Color.BLACK, Square(File.C, 8)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.F, 1), Square(File.G, 2)))

		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.INSUFFICIENT_MATERIAL))
	}

	@Test
	fun `three or more minor pieces still leaves the game ongoing`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Knight(Color.WHITE, Square(File.B, 1)))
		board.place(Knight(Color.WHITE, Square(File.G, 1)))
		board.place(Bishop(Color.WHITE, Square(File.C, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.B, 1), Square(File.A, 3)))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `two knights of the same side does not trigger insufficient material`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Knight(Color.WHITE, Square(File.B, 1)))
		board.place(Knight(Color.WHITE, Square(File.G, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.B, 1), Square(File.A, 3)))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `promotion to a queen keeps the game ongoing`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.A, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 7), hasMoved = true))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.QUEEN))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `bare kings position ends immediately after next move`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.BLACK, Square(File.A, 5)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))
			.makeMove(Move(Square(File.A, 1), Square(File.A, 5)))
			.makeMove(Move(Square(File.E, 8), Square(File.E, 7)))

		val next = game.makeMove(Move(Square(File.A, 5), Square(File.A, 6)))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 8)))
		val ending = next.makeMove(next.availableMoves().first { it.from == Square(File.A, 6) })

		assertThat(ending.status).isIn(GameStatus.ONGOING, GameStatus.FINISHED)
	}
}
