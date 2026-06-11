package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BishopTest {
	
	@Test
	fun `bishop has exactly 4 movement directions`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))
		// when
		val directions = MovementFactory.directionsFor(bishop)
		// then
		assertThat(directions).hasSize(4)
	}
	
	@Test
	fun `all bishop directions have maxRange 7`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))
		// when
		val directions = MovementFactory.directionsFor(bishop)
		// then
		assertThat(directions).allMatch { it.maxRange == 7 }
	}
	
	@Test
	fun `no bishop direction has canJump`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))
		// when
		val directions = MovementFactory.directionsFor(bishop)
		// then
		assertThat(directions).noneMatch { it.canJump }
	}
	
	@Test
	fun `bishop covers 4 diagonal directions`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))
		// when
		val deltas = MovementFactory.directionsFor(bishop).map { it.fileDelta to it.rankDelta }.toSet()
		// then
		assertThat(deltas).containsExactlyInAnyOrder(
			1 to 1, -1 to 1, 1 to -1, -1 to -1
		)
	}
	
	@Test
	fun `moveTo returns new bishop with updated position and hasMoved true`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))
		val target = Square(File.F, 4)
		// when
		val moved = bishop.moveTo(target)
		// then
		assertThat(moved.position).isEqualTo(target)
		assertThat(moved.hasMoved).isTrue()
		assertThat(moved.color).isEqualTo(Color.WHITE)
	}
	
	@Test
	fun `bishop default hasMoved is false`() {
		// given / when
		val bishop = Bishop(Color.BLACK, Square(File.F, 8))
		// then
		assertThat(bishop.hasMoved).isFalse()
	}
}
