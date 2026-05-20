package hwr.oop.examples.template.core

// Stores the turn number and which color has to move.
class Turn(
	val number: Int,
	val color: Color,
) {
	init {
		require(number >= 1) { "Turn number must be at least 1" }
	}
}
