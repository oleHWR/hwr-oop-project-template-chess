package hwr.oop.examples.template.core

enum class MovementUsage {
	MOVE_AND_CAPTURE,
	MOVE_ONLY,
	CAPTURE_ONLY,
}

data class MovementDirection(
	val direction: Direction,
	val maxRange: Int = 7,
	val canJump: Boolean = false,
	val usage: MovementUsage = MovementUsage.MOVE_AND_CAPTURE,
) {
	val fileDelta = direction.fileDelta
	val rankDelta = direction.rankDelta

	fun reaches(fileDistance: Int, rankDistance: Int): Boolean {
		for (steps in 1..maxRange) {
			if (steps * fileDelta == fileDistance && steps * rankDelta == rankDistance) return true
		}
		return false
	}
}

val ROOK_DIRECTIONS = listOf(
	MovementDirection(Direction.UP),
	MovementDirection(Direction.DOWN),
	MovementDirection(Direction.RIGHT),
	MovementDirection(Direction.LEFT)
)

val BISHOP_DIRECTIONS = listOf(
	MovementDirection(Direction.UP_RIGHT),
	MovementDirection(Direction.UP_LEFT),
	MovementDirection(Direction.DOWN_RIGHT),
	MovementDirection(Direction.DOWN_LEFT)
)

val KING_DIRECTIONS = listOf(
	MovementDirection(Direction.UP, maxRange = 1),
	MovementDirection(Direction.DOWN, maxRange = 1),
	MovementDirection(Direction.RIGHT, maxRange = 1),
	MovementDirection(Direction.LEFT, maxRange = 1),
	MovementDirection(Direction.UP_RIGHT, maxRange = 1),
	MovementDirection(Direction.UP_LEFT, maxRange = 1),
	MovementDirection(Direction.DOWN_RIGHT, maxRange = 1),
	MovementDirection(Direction.DOWN_LEFT, maxRange = 1)
)

val KNIGHT_DIRECTIONS = listOf(
	MovementDirection(Direction.UP_UP_RIGHT, maxRange = 1, canJump = true),
	MovementDirection(Direction.UP_UP_LEFT, maxRange = 1, canJump = true),
	MovementDirection(Direction.DOWN_DOWN_RIGHT, maxRange = 1, canJump = true),
	MovementDirection(Direction.DOWN_DOWN_LEFT, maxRange = 1, canJump = true),
	MovementDirection(Direction.RIGHT_RIGHT_UP, maxRange = 1, canJump = true),
	MovementDirection(Direction.RIGHT_RIGHT_DOWN, maxRange = 1, canJump = true),
	MovementDirection(Direction.LEFT_LEFT_UP, maxRange = 1, canJump = true),
	MovementDirection(Direction.LEFT_LEFT_DOWN, maxRange = 1, canJump = true)
)

fun pawnDirections(color: Color, hasMoved: Boolean): List<MovementDirection> {
	val forwardDirection = if (color == Color.WHITE) Direction.UP else Direction.DOWN
	val leftCaptureDirection = if (color == Color.WHITE) Direction.UP_LEFT else Direction.DOWN_LEFT
	val rightCaptureDirection = if (color == Color.WHITE) Direction.UP_RIGHT else Direction.DOWN_RIGHT
	val forwardRange = if (hasMoved) 1 else 2

	return listOf(
		MovementDirection(forwardDirection, maxRange = forwardRange, usage = MovementUsage.MOVE_ONLY),
		MovementDirection(leftCaptureDirection, maxRange = 1, usage = MovementUsage.CAPTURE_ONLY),
		MovementDirection(rightCaptureDirection, maxRange = 1, usage = MovementUsage.CAPTURE_ONLY),
	)
}
