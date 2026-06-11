package hwr.oop.examples.template.core

enum class PieceType {
	PAWN,
	ROOK,
	BISHOP,
	KNIGHT,
	KING,
	QUEEN,
}

interface Piece {
	val type: PieceType
	val color: Color
	val position: Square
	val hasMoved: Boolean
	val uppercaseSymbol: String

	fun moveTo(target: Square): Piece

	fun canCapture(target: Piece): Boolean {
		return MovementFactory.canCapture(this, target)
	}

	fun symbol(): String {
		return if (color == Color.WHITE) uppercaseSymbol else uppercaseSymbol.lowercase()
	}
}
