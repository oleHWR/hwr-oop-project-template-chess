package hwr.oop.examples.template.core

data class Queen(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val uppercaseSymbol = "Q"
	
	override fun directions() = ROOK_DIRECTIONS + BISHOP_DIRECTIONS
	
	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
