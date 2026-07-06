package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
class GameID(
	val value: String,
) {
	init {
		require(value.isNotBlank()) { "Game ID must not be blank" }
	}
}
