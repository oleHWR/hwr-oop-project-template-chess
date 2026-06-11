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
}
