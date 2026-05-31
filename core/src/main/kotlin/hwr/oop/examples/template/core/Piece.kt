package hwr.oop.examples.template.core

interface Piece {
	val color: Color
	val position: Square
	val hasMoved: Boolean
	val uppercaseSymbol: String

	fun directions(): List<MovementDirection>
	fun moveTo(target: Square): Piece

	// Checks capture geometry only; board-level validation handles blockers.
	fun canCapture(target: Piece): Boolean {
		if (target.color == color) return false
		val fileDistance = target.position.file.ordinal - position.file.ordinal
		val rankDistance = target.position.rank - position.rank
		return directions()
			.filterNot { it.usage == MovementUsage.MOVE_ONLY }
			.any { it.reaches(fileDistance, rankDistance) }
	}

	// for Debugging: Returns the letter used when the piece is shown on the board.
	fun symbol(): String {
		return if (color == Color.WHITE) uppercaseSymbol else uppercaseSymbol.lowercase()
	}
}
