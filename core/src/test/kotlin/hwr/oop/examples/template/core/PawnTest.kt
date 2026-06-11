package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PawnTest {

	@Test
	fun `pawn has 3 movement directions`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		// when
		val directions = MovementFactory.directionsFor(pawn)
		// then
		assertThat(directions).hasSize(3)
	}

	@Test
	fun `no pawn direction has canJump`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		// when
		val directions = MovementFactory.directionsFor(pawn)
		// then
		assertThat(directions).noneMatch { it.canJump }
	}

	@Test
	fun `white pawn forward direction goes up and is move only`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		// when
		val forward = MovementFactory.directionsFor(pawn).single { it.usage == MovementUsage.MOVE_ONLY }
		// then
		assertThat(forward.fileDelta).isEqualTo(0)
		assertThat(forward.rankDelta).isEqualTo(1)
		assertThat(forward.usage).isEqualTo(MovementUsage.MOVE_ONLY)
	}

	@Test
	fun `black pawn forward direction goes down and is move only`() {
		// given
		val pawn = Pawn(Color.BLACK, Square(File.E, 7))
		// when
		val forward = MovementFactory.directionsFor(pawn).single { it.usage == MovementUsage.MOVE_ONLY }
		// then
		assertThat(forward.fileDelta).isEqualTo(0)
		assertThat(forward.rankDelta).isEqualTo(-1)
		assertThat(forward.usage).isEqualTo(MovementUsage.MOVE_ONLY)
	}

	@Test
	fun `white pawn has two diagonal capture directions going up`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		// when
		val captures = MovementFactory.directionsFor(pawn).filter { it.usage == MovementUsage.CAPTURE_ONLY }
		// then
		assertThat(captures).hasSize(2)
		assertThat(captures.map { it.fileDelta to it.rankDelta }.toSet())
			.containsExactlyInAnyOrder(1 to 1, -1 to 1)
		assertThat(captures).allMatch { it.maxRange == 1 }
		assertThat(captures).allMatch { it.usage == MovementUsage.CAPTURE_ONLY }
	}

	@Test
	fun `black pawn has two diagonal capture directions going down`() {
		// given
		val pawn = Pawn(Color.BLACK, Square(File.E, 7))
		// when
		val captures = MovementFactory.directionsFor(pawn).filter { it.usage == MovementUsage.CAPTURE_ONLY }
		// then
		assertThat(captures.map { it.fileDelta to it.rankDelta }.toSet())
			.containsExactlyInAnyOrder(1 to -1, -1 to -1)
	}

	@Test
	fun `pawn that has not moved can advance two squares`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		// when
		val forward = MovementFactory.directionsFor(pawn).single { it.usage == MovementUsage.MOVE_ONLY }
		// then
		assertThat(forward.maxRange).isEqualTo(2)
	}

	@Test
	fun `pawn that has moved can only advance one square`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true)
		// when
		val forward = MovementFactory.directionsFor(pawn).single { it.usage == MovementUsage.MOVE_ONLY }
		// then
		assertThat(forward.maxRange).isEqualTo(1)
	}

	@Test
	fun `moveTo returns new pawn with updated position and hasMoved true`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		val target = Square(File.E, 4)
		// when
		val moved = pawn.moveTo(target)
		// then
		assertThat(moved.position).isEqualTo(target)
		assertThat(moved.hasMoved).isTrue()
		assertThat(moved.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `pawn default hasMoved is false`() {
		// given / when
		val pawn = Pawn(Color.BLACK, Square(File.E, 7))
		// then
		assertThat(pawn.hasMoved).isFalse()
	}
}
