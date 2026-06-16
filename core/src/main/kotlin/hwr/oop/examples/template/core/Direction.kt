package hwr.oop.examples.template.core

enum class Direction(
	val fileDelta: Int,
	val rankDelta: Int,
) {
	UP(0, 1),
	DOWN(0, -1),
	RIGHT(1, 0),
	LEFT(-1, 0),
	UP_RIGHT(1, 1),
	UP_LEFT(-1, 1),
	DOWN_RIGHT(1, -1),
	DOWN_LEFT(-1, -1);

	operator fun plus(other: Direction): Pair<Int, Int> =
		(fileDelta + other.fileDelta) to (rankDelta + other.rankDelta)
}
