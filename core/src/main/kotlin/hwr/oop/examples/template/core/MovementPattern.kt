package hwr.oop.examples.template.core

data class MovementPattern(
    val fileDelta: Int,
    val rankDelta: Int,
    val maxRange: Int = 7,
    val canJump: Boolean = false,
    val captureOnly: Boolean = false,
    val moveOnly: Boolean = false
)

val ROOK_PATTERNS = listOf(
    MovementPattern(0, 1),
    MovementPattern(0, -1),
    MovementPattern(1, 0),
    MovementPattern(-1, 0)
)

val BISHOP_PATTERNS = listOf(
    MovementPattern(1, 1),
    MovementPattern(-1, 1),
    MovementPattern(1, -1),
    MovementPattern(-1, -1)
)