package hwr.oop.examples.template.core

class GameID(
	val value: String,
) {
	init {
		require(value.isNotBlank()) { "Game ID must not be blank" }
	}
}
