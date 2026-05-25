package hwr.oop.examples.template.core

data class Pawn(
	override val color: Color,
	override val position: Square,
	override val hasMoved: Boolean = false,
) : Piece {
	override val uppercaseSymbol = "P"

	override fun directions(): List<MovementDirection> {
		val forwardDirection = if (color == Color.WHITE) Direction.UP else Direction.DOWN
		val leftCaptureDirection = if (color == Color.WHITE) Direction.UP_LEFT else Direction.DOWN_LEFT
		val rightCaptureDirection = if (color == Color.WHITE) Direction.UP_RIGHT else Direction.DOWN_RIGHT
		val forwardRange = if (hasMoved) 1 else 2

		return listOf(
			MovementDirection(forwardDirection, maxRange = forwardRange, moveOnly = true),
			MovementDirection(leftCaptureDirection, maxRange = 1, captureOnly = true),
			MovementDirection(rightCaptureDirection, maxRange = 1, captureOnly = true),
		)
	}

	override fun moveTo(target: Square) = copy(position = target, hasMoved = true)
}
