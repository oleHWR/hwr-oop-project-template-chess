package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameStatusTest {
	
	@Test
	fun `game status has expected values`() {
		// given / when
		val statuses = GameStatus.entries
		
		// then
		assertThat(statuses).contains(
			GameStatus.ONGOING,
			GameStatus.FINISHED
		)
	}

	@Test
	fun `position status has expected values`() {
		// given / when
		val statuses = PositionStatus.entries

		// then
		assertThat(statuses).contains(
			PositionStatus.NORMAL,
			PositionStatus.CHECK
		)
	}

	@Test
	fun `game end reason has expected values`() {
		// given / when
		val reasons = GameEndReason.entries

		// then
		assertThat(reasons).contains(
			GameEndReason.CHECKMATE,
			GameEndReason.STALEMATE,
			GameEndReason.DRAW_ACCEPTED,
			GameEndReason.RESIGNED
		)
	}

	@Test
	fun `game result stores reason and winner`() {
		// given
		val result = GameResult(GameEndReason.CHECKMATE, Color.WHITE)

		// when / then
		assertThat(result.reason).isEqualTo(GameEndReason.CHECKMATE)
		assertThat(result.winner).isEqualTo(Color.WHITE)
	}

	@Test
	fun `game result with no winner defaults to null`() {
		// given
		val result = GameResult(GameEndReason.STALEMATE)

		// when / then
		assertThat(result.reason).isEqualTo(GameEndReason.STALEMATE)
		assertThat(result.winner).isNull()
	}
}
