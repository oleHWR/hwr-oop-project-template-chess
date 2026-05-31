package hwr.oop.examples.template.core

class Turn(
	val number: Int,
	val color: Color,
) {
	init {
		require(number >= 1) { "Turn number must be at least 1" }
	}
}
