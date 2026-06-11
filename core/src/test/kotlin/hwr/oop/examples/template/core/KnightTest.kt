package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KnightTest {

	@Test
	fun `knight has exactly 8 movement directions`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		// when
		val directions = MovementFactory.directionsFor(knight)
		// then
		assertThat(directions).hasSize(8)
	}

	@Test
	fun `all knight directions have maxRange 1`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		// when
		val directions = MovementFactory.directionsFor(knight)
		// then
		assertThat(directions).allMatch { it.maxRange == 1 }
	}

	@Test
	fun `all knight directions have canJump`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		// when
		val directions = MovementFactory.directionsFor(knight)
		// then
		assertThat(directions).allMatch { it.canJump }
	}

	@Test
	fun `knight directions can move and capture`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		// when
		val directions = MovementFactory.directionsFor(knight)
		// then
		assertThat(directions).allMatch { it.usage == MovementUsage.MOVE_AND_CAPTURE }
	}

	@Test
	fun `knight covers 8 L-shaped directions`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		// when
		val deltas = MovementFactory.directionsFor(knight).map { it.fileDelta to it.rankDelta }.toSet()
		// then
		assertThat(deltas).containsExactlyInAnyOrder(
			1 to 2, -1 to 2, 1 to -2, -1 to -2,
			2 to 1, 2 to -1, -2 to 1, -2 to -1
		)
	}

	@Test
	fun `moveTo returns new knight with updated position and hasMoved true`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		val target = Square(File.C, 3)
		// when
		val moved = knight.moveTo(target)
		// then
		assertThat(moved.position).isEqualTo(target)
		assertThat(moved.hasMoved).isTrue()
		assertThat(moved.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `knight default hasMoved is false`() {
		// given / when
		val knight = Knight(Color.BLACK, Square(File.G, 8))
		// then
		assertThat(knight.hasMoved).isFalse()
	}
}
