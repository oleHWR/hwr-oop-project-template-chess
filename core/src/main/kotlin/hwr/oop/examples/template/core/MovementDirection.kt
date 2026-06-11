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
