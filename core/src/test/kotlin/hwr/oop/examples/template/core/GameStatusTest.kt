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
			GameStatus.CHECK,
			GameStatus.CHECKMATE,
			GameStatus.STALEMATE,
			GameStatus.DRAW,
			GameStatus.RESIGNED
		)
	}
}
