package hwr.oop.examples.template.core

data class Rook(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val type = PieceType.ROOK
	override val uppercaseSymbol = "R"
	
	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
