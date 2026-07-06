package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OfferDrawTest {

	@Test
	fun `offerDraw stores the offering player in pendingDrawOfferBy`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val withOffer = game.offerDraw(Color.WHITE)
		
		// then
		assertThat(withOffer.pendingDrawOfferBy).isEqualTo(Color.WHITE)
		assertThat(withOffer.status).isEqualTo(GameStatus.ONGOING)
		assertThat(withOffer.result).isNull()
	}

	@Test
	fun `offerDraw fails when only the side to move may offer`() {
		// given - white to move
		val game = Game(GameID("game-1"))
		
		// when / then - black cannot offer a draw out of turn
		assertThatThrownBy { game.offerDraw(Color.BLACK) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Only the side to move may offer a draw")
	}

	@Test
	fun `offerDraw fails when an offer is already pending`() {
		// given
		val game = Game(GameID("game-1")).offerDraw(Color.WHITE)
		
		// when / then
		assertThatThrownBy { game.offerDraw(Color.WHITE) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("A draw offer is already pending")
	}

	@Test
	fun `offerDraw fails when the game is finished`() {
		// given
		val game = Game(
			id = GameID("game-1"),
			status = GameStatus.FINISHED,
			result = GameResult(GameEndReason.RESIGNED, Color.BLACK)
		)
		
		// when / then
		assertThatThrownBy { game.offerDraw(Color.WHITE) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Game is not in progress")
	}
}
