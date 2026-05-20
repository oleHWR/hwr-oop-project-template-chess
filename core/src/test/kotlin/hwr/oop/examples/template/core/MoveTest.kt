package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MoveTest {
	
	@Test
	fun `move stores start and target square`() {
		// given
		val from = Square(File.E, 1)
		val to = Square(File.E, 2)
		
		// when
		val move = Move(from, to)
		
		// then
		assertThat(move.from).isEqualTo(from)
		assertThat(move.to).isEqualTo(to)
	}
}
