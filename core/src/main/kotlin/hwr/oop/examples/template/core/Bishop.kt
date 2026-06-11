package hwr.oop.examples.template.core

data class Bishop(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val type = PieceType.BISHOP
	override val uppercaseSymbol = "B"
	
	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
