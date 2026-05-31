package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MovementDirectionTest {
	
	@Test
	fun `movement direction stores named direction and max range`() {
		// given
		val namedDirection = Direction.DOWN_RIGHT
		val maxRange = 7
		
		// when
		val movementDirection = MovementDirection(namedDirection, maxRange = maxRange)
		
		// then
		assertThat(movementDirection.direction).isEqualTo(Direction.DOWN_RIGHT)
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
	fun `movement direction calculates board deltas`() {
		// given / when
		val direction = MovementDirection(Direction.DOWN_RIGHT)
		
		// then
		assertThat(direction.fileDelta).isEqualTo(1)
		assertThat(direction.rankDelta).isEqualTo(-1)
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
	fun `direction enum supports compound directions`() {
		// given / when
		val direction = Direction.UP_UP_RIGHT
		
		// then
		assertThat(direction.fileDelta).isEqualTo(1)
		assertThat(direction.rankDelta).isEqualTo(2)
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
