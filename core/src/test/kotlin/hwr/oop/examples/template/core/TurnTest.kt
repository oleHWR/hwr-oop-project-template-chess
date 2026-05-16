package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class TurnTest {
	
	@Test
	fun `turn stores number and color`() {
		// given
		val number = 4
		val color = Color.BLACK
		
		// when
		val turn = Turn(number, color)
		
		// then
		assertThat(turn.number).isEqualTo(4)
		assertThat(turn.color).isEqualTo(Color.BLACK)
	}
	
	@Test
	fun `turn fails when number is smaller than 1`() {
		// given
		val number = 0
		
		// when / then
		assertThatThrownBy { Turn(number, Color.WHITE) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Turn number must be at least 1")
	}
}
