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
	DOWN_LEFT(-1, -1),
	UP_UP_RIGHT(1, 2),
	UP_UP_LEFT(-1, 2),
	DOWN_DOWN_RIGHT(1, -2),
	DOWN_DOWN_LEFT(-1, -2),
	RIGHT_RIGHT_UP(2, 1),
	RIGHT_RIGHT_DOWN(2, -1),
	LEFT_LEFT_UP(-2, 1),
	LEFT_LEFT_DOWN(-2, -1),
}
