package hwr.oop.examples.template.core

data class King(
    override val color: Color,
    override val position: Square,
    override val hasMoved: Boolean = false
) : Piece() {
    override fun movementPatterns() = listOf(
        MovementPattern(0, 1, maxRange = 1),
        MovementPattern(0, -1, maxRange = 1),
        MovementPattern(1, 0, maxRange = 1),
        MovementPattern(-1, 0, maxRange = 1),
        MovementPattern(1, 1, maxRange = 1),
        MovementPattern(-1, 1, maxRange = 1),
        MovementPattern(1, -1, maxRange = 1),
        MovementPattern(-1, -1, maxRange = 1)
    )

    override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
