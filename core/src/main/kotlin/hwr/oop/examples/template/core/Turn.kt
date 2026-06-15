package hwr.oop.examples.template.core

class Turn(
	val number: Int,
	val color: Color,
) {
	init {
		require(number >= 1) { "Turn number must be at least 1" }
	}

	fun next(): Turn {
		val nextColor = if (color == Color.WHITE) Color.BLACK else Color.WHITE
		val nextNumber = if (color == Color.BLACK) number + 1 else number
		return Turn(nextNumber, nextColor)
	}
}
