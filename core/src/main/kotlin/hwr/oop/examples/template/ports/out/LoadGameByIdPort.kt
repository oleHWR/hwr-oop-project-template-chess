package hwr.oop.examples.template.ports.out

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID

interface LoadGameByIdPort {
	fun loadById(gameId: GameID): Game

	class CouldNotLoadException(
		gameId: GameID,
		cause: Exception? = null,
	) : RuntimeException(
		"Could not load game with id: ${gameId.value}",
		cause,
	)
}
