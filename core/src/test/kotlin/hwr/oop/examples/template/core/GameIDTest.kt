package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class GameIDTest {
	
	@Test
	fun `game id stores value`() {
		// given
		val value = "game-1"
		
		// when
		val gameID = GameID(value)
		
		// then
		assertThat(gameID.value).isEqualTo("game-1")
	}
	
	@Test
	fun `game id fails when value is blank`() {
		// given
		val value = ""
		
		// when / then
		assertThatThrownBy { GameID(value) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Game ID must not be blank")
	}
}
