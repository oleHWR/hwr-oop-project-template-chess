package hwr.oop.examples.template.core

interface Piece {
	val color: Color
	val position: Square
	val hasMoved: Boolean
	val uppercaseSymbol: String
	
	fun directions(): List<MovementDirection>
	fun moveTo(target: Square): Piece
	
	// for Debugging: Returns the letter used when the piece is shown on the board.
	fun symbol(): String {
		return if (color == Color.WHITE) uppercaseSymbol else uppercaseSymbol.lowercase()
	}
}
