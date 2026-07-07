package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EnPassantTest {

	@Test
	fun `new game has no en passant target`() {
		val game = Game(GameID("game-1"))

		assertThat(game.enPassantTarget).isNull()
	}

	@Test
	fun `white double pawn push sets en passant target on rank 3`() {
		val game = Game(GameID("game-1"))

		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		assertThat(next.enPassantTarget).isEqualTo(Square(File.E, 3))
	}

	@Test
	fun `black double pawn push sets en passant target on rank 6`() {
		val game = Game(GameID("game-1"), turn = Turn(1, Color.BLACK))

		val next = game.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))

		assertThat(next.enPassantTarget).isEqualTo(Square(File.E, 6))
	}

	@Test
	fun `single pawn push does not set en passant target`() {
		val game = Game(GameID("game-1"))

		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 3)))

		assertThat(next.enPassantTarget).isNull()
	}

	@Test
	fun `en passant target clears after a non-double-push move`() {
		val game = Game(GameID("game-1"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.A, 7), Square(File.A, 6)))

		assertThat(game.enPassantTarget).isNull()
	}

	@Test
	fun `white pawn can capture en passant`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 5)))
		board.place(Pawn(Color.BLACK, Square(File.D, 7)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.BLACK))

		val afterDouble = game.makeMove(Move(Square(File.D, 7), Square(File.D, 5)))
		assertThat(afterDouble.enPassantTarget).isEqualTo(Square(File.D, 6))

		val enPassant = Move(Square(File.E, 5), Square(File.D, 6))
		assertThat(afterDouble.availableMoves()).contains(enPassant)

		val next = afterDouble.makeMove(enPassant)

		assertThat(next.board.pieceAt(Square(File.D, 6)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.D, 6), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.D, 5))).isNull()
		assertThat(next.board.pieceAt(Square(File.E, 5))).isNull()
	}

	@Test
	fun `black pawn can capture en passant`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.BLACK, Square(File.E, 4)))
		board.place(Pawn(Color.WHITE, Square(File.D, 2)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))

		val afterDouble = game.makeMove(Move(Square(File.D, 2), Square(File.D, 4)))
		val enPassant = Move(Square(File.E, 4), Square(File.D, 3))
		assertThat(afterDouble.availableMoves()).contains(enPassant)

		val next = afterDouble.makeMove(enPassant)

		assertThat(next.board.pieceAt(Square(File.D, 3)))
			.isEqualTo(Pawn(Color.BLACK, Square(File.D, 3), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.D, 4))).isNull()
	}

	@Test
	fun `en passant window expires after one move`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 5)))
		board.place(Pawn(Color.BLACK, Square(File.D, 7)))
		board.place(Pawn(Color.WHITE, Square(File.A, 2)))
		board.place(Pawn(Color.BLACK, Square(File.H, 7)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.BLACK))

		val afterDouble = game.makeMove(Move(Square(File.D, 7), Square(File.D, 5)))
		val skipped = afterDouble
			.makeMove(Move(Square(File.A, 2), Square(File.A, 3)))
			.makeMove(Move(Square(File.H, 7), Square(File.H, 6)))

		assertThat(skipped.enPassantTarget).isNull()
		assertThat(skipped.availableMoves()).doesNotContain(
			Move(Square(File.E, 5), Square(File.D, 6))
		)
	}

	@Test
	fun `en passant capture resets the halfmove clock`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 5)))
		board.place(Pawn(Color.BLACK, Square(File.D, 7)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.BLACK), halfmoveClock = 40)

		val afterDouble = game.makeMove(Move(Square(File.D, 7), Square(File.D, 5)))
		val next = afterDouble.makeMove(Move(Square(File.E, 5), Square(File.D, 6)))

		assertThat(next.halfmoveClock).isEqualTo(0)
	}

	@Test
	fun `en passant is not offered when the double-pushed pawn belongs to the same side`() {
		val game = Game(GameID("game-1"), enPassantTarget = Square(File.E, 3))

		assertThat(game.availableMoves()).noneMatch { it.to == Square(File.E, 3) && it.from.rank == 4 }
	}

	@Test
	fun `en passant cannot be played when it exposes own king`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 5)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 5)))
		board.place(Pawn(Color.BLACK, Square(File.F, 7)))
		board.place(Rook(Color.BLACK, Square(File.H, 5)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.BLACK))

		val afterDouble = game.makeMove(Move(Square(File.F, 7), Square(File.F, 5)))

		assertThatThrownBy {
			afterDouble.makeMove(Move(Square(File.E, 5), Square(File.F, 6)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")
	}

	@Test
	fun `en passant target survives through draw offer round-trip`() {
		val game = Game(GameID("game-1"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		val offered = game.offerDraw(Color.BLACK)
		val declined = offered.declineDraw()

		assertThat(offered.enPassantTarget).isEqualTo(Square(File.E, 3))
		assertThat(declined.enPassantTarget).isEqualTo(Square(File.E, 3))
	}

	@Test
	fun `en passant target survives through accept draw and resign`() {
		val game = Game(GameID("game-1"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		val accepted = game.offerDraw(Color.BLACK).acceptDraw()
		val resigned = game.resign(Color.BLACK)

		assertThat(accepted.enPassantTarget).isEqualTo(Square(File.E, 3))
		assertThat(resigned.enPassantTarget).isEqualTo(Square(File.E, 3))
	}

	@Test
	fun `en passant is not offered when no adjacent friendly pawn exists`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.A, 2)))
		val game = Game(
			GameID("game-1"), board, Turn(1, Color.WHITE),
			enPassantTarget = Square(File.H, 6),
		)

		assertThat(game.availableMoves()).noneMatch { it.to == Square(File.H, 6) }
	}

	@Test
	fun `diagonal pawn capture on an occupied en passant target square is a normal capture`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.BLACK, Square(File.A, 8)))
		board.place(Pawn(Color.WHITE, Square(File.E, 5)))
		board.place(Pawn(Color.BLACK, Square(File.F, 6)))
		// Distinguishing piece on the would-be ep victim square: mutant would remove
		// it, real code must not touch it.
		board.place(Rook(Color.BLACK, Square(File.F, 5)))
		val game = Game(
			GameID("game-1"), board, Turn(1, Color.WHITE),
			enPassantTarget = Square(File.F, 6),
		)

		val next = game.makeMove(Move(Square(File.E, 5), Square(File.F, 6)))

		assertThat(next.board.pieceAt(Square(File.F, 6)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.F, 6), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.F, 5)))
			.isEqualTo(Rook(Color.BLACK, Square(File.F, 5)))
	}
}
