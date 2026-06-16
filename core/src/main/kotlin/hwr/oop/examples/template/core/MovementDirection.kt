package hwr.oop.examples.template.core

enum class MovementUsage {
	MOVE_AND_CAPTURE,
	MOVE_ONLY,
	CAPTURE_ONLY,
}

data class MovementDirection(
	val fileDelta: Int,
	val rankDelta: Int,
	val maxRange: Int,
	val canJump: Boolean,
	val usage: MovementUsage,
) {
	constructor(
		direction: Direction,
		maxRange: Int = 7,
		canJump: Boolean = false,
		usage: MovementUsage = MovementUsage.MOVE_AND_CAPTURE,
	) : this(direction.fileDelta, direction.rankDelta, maxRange, canJump, usage)

	constructor(
		deltas: Pair<Int, Int>,
		maxRange: Int = 7,
		canJump: Boolean = false,
		usage: MovementUsage = MovementUsage.MOVE_AND_CAPTURE,
	) : this(deltas.first, deltas.second, maxRange, canJump, usage)

	fun reaches(fileDistance: Int, rankDistance: Int): Boolean {
		for (steps in 1..maxRange) {
			if (steps * fileDelta == fileDistance && steps * rankDelta == rankDistance) return true
		}
		return false
	}
}
