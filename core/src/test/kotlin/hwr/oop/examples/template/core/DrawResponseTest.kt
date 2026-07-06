package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class DrawResponseTest {

	@Test
	fun `declineDraw removes the pending offer`() {
		// given
		val game = Game(GameID("game-1")).offerDraw(Color.WHITE)
		
		// when
		val declined = game.declineDraw()
		
		// then
		assertThat(declined.pendingDrawOfferBy).isNull()
		assertThat(declined.status).isEqualTo(GameStatus.ONGOING)
		assertThat(declined.result).isNull()
	}

	@Test
	fun `declineDraw fails when there is no pending offer`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when / then
		assertThatThrownBy { game.declineDraw() }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("No draw offer to respond to")
	}

	@Test
	fun `acceptDraw finishes the game with DRAW_ACCEPTED and no winner`() {
		// given
		val game = Game(GameID("game-1")).offerDraw(Color.WHITE)
		
		// when
		val accepted = game.acceptDraw()
		
		// then
		assertThat(accepted.status).isEqualTo(GameStatus.FINISHED)
		assertThat(accepted.result).isEqualTo(GameResult(GameEndReason.DRAW_ACCEPTED))
		assertThat(accepted.result?.winner).isNull()
		assertThat(accepted.pendingDrawOfferBy).isNull()
	}

	@Test
	fun `acceptDraw fails when there is no pending offer`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when / then
		assertThatThrownBy { game.acceptDraw() }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("No draw offer to respond to")
	}

	@Test
	fun `accepted draw leaves the board untouched`() {
		// given
		val game = Game(GameID("game-1")).offerDraw(Color.WHITE)
		
		// when
		val accepted = game.acceptDraw()
		
		// then - same starting setup, no piece movement happened
		assertThat(accepted.board).isSameAs(game.board)
		assertThat(accepted.board.pieceAt(Square(File.E, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 2)))
	}
}
