package hwr.oop.examples.template.core

data class King(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val uppercaseSymbol = "K"
	
	override fun directions() = KING_DIRECTIONS
	
	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
