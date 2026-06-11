package hwr.oop.examples.template.core

data class King(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val type = PieceType.KING
	override val uppercaseSymbol = "K"
	
	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
