package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColorTest {

	@Test
	fun `opposite of white is black`() {
		assertThat(Color.WHITE.opposite()).isEqualTo(Color.BLACK)
	}

	@Test
	fun `opposite of black is white`() {
		assertThat(Color.BLACK.opposite()).isEqualTo(Color.WHITE)
	}
}
