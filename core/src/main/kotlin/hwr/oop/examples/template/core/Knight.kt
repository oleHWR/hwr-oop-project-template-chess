package hwr.oop.examples.template.core

data class Knight(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val type = PieceType.KNIGHT
	override val uppercaseSymbol = "N"

	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
