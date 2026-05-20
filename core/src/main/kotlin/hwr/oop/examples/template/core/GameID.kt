package hwr.oop.examples.template.core

// Identifies one saved or running game.
class GameID(
	val value: String,
) {
	init {
		require(value.isNotBlank()) { "Game ID must not be blank" }
	}
}
