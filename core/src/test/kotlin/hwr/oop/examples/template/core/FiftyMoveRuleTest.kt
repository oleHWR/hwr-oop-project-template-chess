package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class FiftyMoveRuleTest {

	@Test
	fun `new game starts with halfmove clock at zero`() {
		val game = Game(GameID("game-1"))

		assertThat(game.halfmoveClock).isEqualTo(0)
	}

	@Test
	fun `halfmove clock increments after a non-pawn non-capture move`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.B, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))

		val next = game.makeMove(Move(Square(File.B, 1), Square(File.B, 3)))

		assertThat(next.halfmoveClock).isEqualTo(1)
	}

	@Test
	fun `halfmove clock resets to zero after a pawn move`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.A, 2)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE), halfmoveClock = 30)

		val next = game.makeMove(Move(Square(File.A, 2), Square(File.A, 3)))

		assertThat(next.halfmoveClock).isEqualTo(0)
	}

	@Test
	fun `halfmove clock resets to zero after a capture`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Knight(Color.BLACK, Square(File.A, 5)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE), halfmoveClock = 42)

		val next = game.makeMove(Move(Square(File.A, 1), Square(File.A, 5)))

		assertThat(next.halfmoveClock).isEqualTo(0)
	}

	@Test
	fun `game ends in FIFTY_MOVE_RULE draw when clock reaches 100`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.B, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE), halfmoveClock = 99)

		val next = game.makeMove(Move(Square(File.B, 1), Square(File.B, 3)))

		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.FIFTY_MOVE_RULE))
		assertThat(next.result?.winner).isNull()
		assertThat(next.halfmoveClock).isEqualTo(100)
	}

	@Test
	fun `game does not end at halfmove clock 99`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.B, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE), halfmoveClock = 98)

		val next = game.makeMove(Move(Square(File.B, 1), Square(File.B, 3)))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
		assertThat(next.result).isNull()
		assertThat(next.halfmoveClock).isEqualTo(99)
	}

	@Test
	fun `pawn move that would reach clock 100 does not end game because clock resets`() {
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Pawn(Color.WHITE, Square(File.A, 2)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE), halfmoveClock = 99)

		val next = game.makeMove(Move(Square(File.A, 2), Square(File.A, 3)))

		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
		assertThat(next.halfmoveClock).isEqualTo(0)
	}

	@Test
	fun `checkmate takes priority over the fifty move rule`() {
		val board = Board()
		board.place(King(Color.BLACK, Square(File.H, 8)))
		board.place(Pawn(Color.BLACK, Square(File.F, 7)))
		board.place(Pawn(Color.BLACK, Square(File.G, 7)))
		board.place(Pawn(Color.BLACK, Square(File.H, 7)))
		board.place(Rook(Color.WHITE, Square(File.D, 1)))
		board.place(King(Color.WHITE, Square(File.A, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE), halfmoveClock = 99)

		val next = game.makeMove(Move(Square(File.D, 1), Square(File.D, 8)))

		assertThat(next.result).isEqualTo(GameResult(GameEndReason.CHECKMATE, Color.WHITE))
	}

	@Test
	fun `negative halfmove clock is rejected`() {
		assertThatThrownBy {
			Game(GameID("game-1"), halfmoveClock = -1)
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Halfmove clock must not be negative")
	}

	@Test
	fun `halfmove clock survives through offer, decline and accept draw`() {
		val game = Game(GameID("game-1"), halfmoveClock = 17)

		val offered = game.offerDraw(Color.WHITE)
		assertThat(offered.halfmoveClock).isEqualTo(17)

		val declined = offered.declineDraw()
		assertThat(declined.halfmoveClock).isEqualTo(17)

		val accepted = offered.acceptDraw()
		assertThat(accepted.halfmoveClock).isEqualTo(17)
	}

	@Test
	fun `halfmove clock survives through resign`() {
		val game = Game(GameID("game-1"), halfmoveClock = 25)

		val resigned = game.resign(Color.WHITE)

		assertThat(resigned.halfmoveClock).isEqualTo(25)
	}
}
