package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CastlingTest {

	private fun castlingBoard(color: Color): Board {
		val rank = if (color == Color.WHITE) 1 else 8
		val other = if (color == Color.WHITE) 8 else 1
		val board = Board()
		board.place(King(color, Square(File.E, rank)))
		board.place(Rook(color, Square(File.A, rank)))
		board.place(Rook(color, Square(File.H, rank)))
		board.place(King(color.opposite(), Square(File.E, other)))
		return board
	}

	@Test
	fun `white can castle kingside`() {
		val game = Game(GameID("g"), castlingBoard(Color.WHITE), Turn(1, Color.WHITE))
		val castle = Move(Square(File.E, 1), Square(File.G, 1))

		assertThat(game.availableMoves()).contains(castle)
		val next = game.makeMove(castle)

		assertThat(next.board.pieceAt(Square(File.G, 1)))
			.isEqualTo(King(Color.WHITE, Square(File.G, 1), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.F, 1)))
			.isEqualTo(Rook(Color.WHITE, Square(File.F, 1), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.E, 1))).isNull()
		assertThat(next.board.pieceAt(Square(File.H, 1))).isNull()
	}

	@Test
	fun `white can castle queenside`() {
		val game = Game(GameID("g"), castlingBoard(Color.WHITE), Turn(1, Color.WHITE))
		val castle = Move(Square(File.E, 1), Square(File.C, 1))

		val next = game.makeMove(castle)

		assertThat(next.board.pieceAt(Square(File.C, 1)))
			.isEqualTo(King(Color.WHITE, Square(File.C, 1), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.D, 1)))
			.isEqualTo(Rook(Color.WHITE, Square(File.D, 1), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.A, 1))).isNull()
	}

	@Test
	fun `black can castle kingside`() {
		val game = Game(GameID("g"), castlingBoard(Color.BLACK), Turn(1, Color.BLACK))
		val castle = Move(Square(File.E, 8), Square(File.G, 8))

		val next = game.makeMove(castle)

		assertThat(next.board.pieceAt(Square(File.G, 8)))
			.isEqualTo(King(Color.BLACK, Square(File.G, 8), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.F, 8)))
			.isEqualTo(Rook(Color.BLACK, Square(File.F, 8), hasMoved = true))
	}

	@Test
	fun `black can castle queenside`() {
		val game = Game(GameID("g"), castlingBoard(Color.BLACK), Turn(1, Color.BLACK))
		val castle = Move(Square(File.E, 8), Square(File.C, 8))

		val next = game.makeMove(castle)

		assertThat(next.board.pieceAt(Square(File.C, 8)))
			.isEqualTo(King(Color.BLACK, Square(File.C, 8), hasMoved = true))
		assertThat(next.board.pieceAt(Square(File.D, 8)))
			.isEqualTo(Rook(Color.BLACK, Square(File.D, 8), hasMoved = true))
	}

	@Test
	fun `cannot castle when king has moved`() {
		val board = castlingBoard(Color.WHITE)
		board.remove(Square(File.E, 1))
		board.place(King(Color.WHITE, Square(File.E, 1), hasMoved = true))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) &&
					(it.to == Square(File.G, 1) || it.to == Square(File.C, 1))
		}
	}

	@Test
	fun `cannot castle kingside when the rook has moved`() {
		val board = castlingBoard(Color.WHITE)
		board.remove(Square(File.H, 1))
		board.place(Rook(Color.WHITE, Square(File.H, 1), hasMoved = true))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.G, 1)
		}
	}

	@Test
	fun `cannot castle queenside when the rook has moved`() {
		val board = castlingBoard(Color.WHITE)
		board.remove(Square(File.A, 1))
		board.place(Rook(Color.WHITE, Square(File.A, 1), hasMoved = true))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.C, 1)
		}
	}

	@Test
	fun `cannot castle when squares between are occupied`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Bishop(Color.WHITE, Square(File.F, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.G, 1)
		}
	}

	@Test
	fun `cannot castle queenside when b1 is occupied`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Knight(Color.WHITE, Square(File.B, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.C, 1)
		}
	}

	@Test
	fun `cannot castle when in check`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Rook(Color.BLACK, Square(File.E, 4)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) &&
					(it.to == Square(File.G, 1) || it.to == Square(File.C, 1))
		}
	}

	@Test
	fun `cannot castle through check`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Rook(Color.BLACK, Square(File.F, 4)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.G, 1)
		}
	}

	@Test
	fun `cannot castle into check`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Rook(Color.BLACK, Square(File.G, 4)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.G, 1)
		}
	}

	@Test
	fun `cannot castle queenside through check on d1`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Rook(Color.BLACK, Square(File.D, 4)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.C, 1)
		}
	}

	@Test
	fun `attempting castle when unavailable is rejected`() {
		val board = castlingBoard(Color.WHITE)
		board.place(Bishop(Color.WHITE, Square(File.F, 1)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThatThrownBy {
			game.makeMove(Move(Square(File.E, 1), Square(File.G, 1)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")
	}

	@Test
	fun `castling increments the halfmove clock`() {
		val game = Game(GameID("g"), castlingBoard(Color.WHITE), Turn(1, Color.WHITE), halfmoveClock = 4)

		val next = game.makeMove(Move(Square(File.E, 1), Square(File.G, 1)))

		assertThat(next.halfmoveClock).isEqualTo(5)
	}

	@Test
	fun `castling does not set an en passant target`() {
		val game = Game(GameID("g"), castlingBoard(Color.WHITE), Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.E, 1), Square(File.G, 1)))

		assertThat(next.enPassantTarget).isNull()
	}

	@Test
	fun `no castling offered when the H1 square has no rook at all`() {
		val board = castlingBoard(Color.WHITE)
		board.remove(Square(File.H, 1))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch {
			it.from == Square(File.E, 1) && it.to == Square(File.G, 1)
		}
	}

	@Test
	fun `no castling offered when E1 does not hold a king`() {
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Rook(Color.WHITE, Square(File.H, 1)))
		board.place(King(Color.WHITE, Square(File.A, 5)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("g"), board, Turn(1, Color.WHITE))

		assertThat(game.availableMoves()).noneMatch { it.from == Square(File.E, 1) }
	}
}
