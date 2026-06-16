package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MovementDirectionTest {

	@Test
	fun `movement direction stores deltas and max range`() {
		// given
		val maxRange = 7

		// when
		val movementDirection = MovementDirection(Direction.DOWN_RIGHT, maxRange = maxRange)

		// then
		assertThat(movementDirection.fileDelta).isEqualTo(1)
		assertThat(movementDirection.rankDelta).isEqualTo(-1)
		assertThat(movementDirection.maxRange).isEqualTo(7)
	}

	@Test
	fun `movement direction defaults maxRange to 7`() {
		// given / when
		val direction = MovementDirection(Direction.RIGHT)

		// then
		assertThat(direction.maxRange).isEqualTo(7)
	}

	@Test
	fun `movement direction defaults canJump to false`() {
		// given / when
		val direction = MovementDirection(Direction.RIGHT)

		// then
		assertThat(direction.canJump).isFalse()
	}

	@Test
	fun `movement direction defaults to move and capture usage`() {
		// given / when
		val direction = MovementDirection(Direction.RIGHT)

		// then
		assertThat(direction.usage).isEqualTo(MovementUsage.MOVE_AND_CAPTURE)
	}

	@Test
	fun `direction enum stores board deltas`() {
		// given / when
		val direction = Direction.UP_LEFT

		// then
		assertThat(direction.fileDelta).isEqualTo(-1)
		assertThat(direction.rankDelta).isEqualTo(1)
	}

	@Test
	fun `direction plus combines fileDelta and rankDelta`() {
		// given / when
		val combined = Direction.UP + Direction.UP_RIGHT

		// then — knight move "up two, right one" expressed as a sum of basic directions
		assertThat(combined).isEqualTo(1 to 2)
	}

	@Test
	fun `direction plus is commutative`() {
		assertThat(Direction.UP + Direction.RIGHT).isEqualTo(Direction.RIGHT + Direction.UP)
	}

	@Test
	fun `movement direction can be constructed from a delta pair`() {
		// given — knight move composed from two basic directions
		val deltas = Direction.RIGHT + Direction.UP_RIGHT

		// when
		val movement = MovementDirection(deltas, maxRange = 1, canJump = true)

		// then — file +2, rank +1 L-shape
		assertThat(movement.fileDelta).isEqualTo(2)
		assertThat(movement.rankDelta).isEqualTo(1)
		assertThat(movement.canJump).isTrue()
	}

	@Test
	fun `movement direction from delta pair uses defaults`() {
		// given / when — exercise the all-defaults path of the Pair-based constructor
		val movement = MovementDirection(Direction.UP + Direction.RIGHT)

		// then
		assertThat(movement.fileDelta).isEqualTo(1)
		assertThat(movement.rankDelta).isEqualTo(1)
		assertThat(movement.maxRange).isEqualTo(7)
		assertThat(movement.canJump).isFalse()
		assertThat(movement.usage).isEqualTo(MovementUsage.MOVE_AND_CAPTURE)
	}

	@Test
	fun `movement direction with max range zero reaches no square`() {
		// given
		val direction = MovementDirection(Direction.RIGHT, maxRange = 0)

		// when
		val reaches = direction.reaches(1, 0)

		// then
		assertThat(reaches).isFalse()
	}
}

