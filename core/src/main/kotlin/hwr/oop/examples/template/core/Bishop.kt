package hwr.oop.examples.template.core

data class Bishop(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val uppercaseSymbol = "B"
	
	override fun directions() = BISHOP_DIRECTIONS
	
	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
