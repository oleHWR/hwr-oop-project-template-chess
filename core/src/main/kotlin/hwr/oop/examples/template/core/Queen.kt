package hwr.oop.examples.template.core

data class Queen(
    override val color: Color,
    override val position: Square,
    override val hasMoved: Boolean = false
) : Piece() {
    override fun movementPatterns() = ROOK_PATTERNS + BISHOP_PATTERNS

    override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
