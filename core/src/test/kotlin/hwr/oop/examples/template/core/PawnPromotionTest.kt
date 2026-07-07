package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PawnPromotionTest {

	private fun promotionBoard(): Board {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.A, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 7), hasMoved = true))
		return board
	}

	@Test
	fun `white pawn reaching rank 8 gets four promotion moves`() {
		val game = Game(GameID("g"), promotionBoard(), Turn(1, Color.WHITE))

		val moves = game.availableMoves().filter { it.from == Square(File.E, 7) && it.to == Square(File.E, 8) }
		val promotions = moves.mapNotNull { it.promotion }

		assertThat(promotions).containsExactlyInAnyOrder(
			PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT
		)
	}

	@Test
	fun `making a queen promotion move replaces the pawn with a queen`() {
		val game = Game(GameID("g"), promotionBoard(), Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.QUEEN))

		assertThat(next.board.pieceAt(Square(File.E, 8)))
			.isEqualTo(Queen(Color.WHITE, Square(File.E, 8), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.E, 7))).isNull()
	}

	@Test
	fun `promotion to knight, rook, bishop each replace the pawn correctly`() {
		val toKnight = Game(GameID("g"), promotionBoard(), Turn(1, Color.WHITE))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.KNIGHT))
		val toRook = Game(GameID("g"), promotionBoard(), Turn(1, Color.WHITE))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.ROOK))
		val toBishop = Game(GameID("g"), promotionBoard(), Turn(1, Color.WHITE))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.BISHOP))

		assertThat(toKnight.board.pieceAt(Square(File.E, 8)))
			.isEqualTo(Knight(Color.WHITE, Square(File.E, 8), hasMoved = true))
		assertThat(toRook.board.pieceAt(Square(File.E, 8)))
			.isEqualTo(Rook(Color.WHITE, Square(File.E, 8), hasMoved = true))
		assertThat(toBishop.board.pieceAt(Square(File.E, 8)))
			.isEqualTo(Bishop(Color.WHITE, Square(File.E, 8), hasMoved = true))
	}

	@Test
	fun `black pawn reaching rank 1 is promoted`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.H, 8)))
		board.place(Pawn(Color.BLACK, Square(File.C, 2), hasMoved = true))
		val game = Game(GameID("g"), board, Turn(1, Color.BLACK))

		val next = game.makeMove(Move(Square(File.C, 2), Square(File.C, 1), PieceType.QUEEN))

		assertThat(next.board.pieceAt(Square(File.C, 1)))
			.isEqualTo(Queen(Color.BLACK, Square(File.C, 1), hasMoved = true))
	}

	@Test
	fun `non-promotion pawn move does not carry a promotion field`() {
		val game = Game(GameID("g"))

		val moves = game.availableMoves().filter { it.from == Square(File.E, 2) }

		assertThat(moves).allMatch { it.promotion == null }
	}

	@Test
	fun `promotion to a king or pawn is rejected as a move`() {
		val game = Game(GameID("g"), promotionBoard(), Turn(1, Color.WHITE))

		assertThatThrownBy {
			game.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.KING))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")

		assertThatThrownBy {
			game.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.PAWN))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")
	}

	@Test
	fun `promotion via a diagonal capture also replaces the pawn`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.H, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 7), hasMoved = true))
		board.place(Rook(Color.BLACK, Square(File.F, 8)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.E, 7), Square(File.F, 8), PieceType.QUEEN))

		assertThat(next.board.pieceAt(Square(File.F, 8)))
			.isEqualTo(Queen(Color.WHITE, Square(File.F, 8), hasMoved = true))
	}

	@Test
	fun `promotion resets the halfmove clock since it is a pawn move`() {
		val game = Game(
			GameID("g"), promotionBoard(), Turn(1, Color.WHITE), halfmoveClock = 40
		)

		val next = game.makeMove(Move(Square(File.E, 7), Square(File.E, 8), PieceType.QUEEN))

		assertThat(next.halfmoveClock).isEqualTo(0)
	}
}