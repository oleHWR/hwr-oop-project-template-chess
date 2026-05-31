package hwr.oop.examples.template.core

data class Pawn(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val uppercaseSymbol = "P"

	override fun directions() = pawnDirections(color, hasMoved)

	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
