package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.util.UUID

class StartGameCommand(
	private val persistence: GameRepository,
) : CliktCommand(name = "startGame") {
	private val gameId by option(
		"--game-id",
		help = "The game ID to use. A random ID is generated when omitted."
	)
	private val whitePlayerId by option(
		"--white-player-id",
		help = "The player ID of the player who will control the white pieces."
	).required()
	private val blackPlayerId by option(
		"--black-player-id",
		help = "The player ID of the player who will control the black pieces."
	).required()
	
	override fun run() {
		val id = gameId ?: UUID.randomUUID().toString()
		val game = Game(
			id = GameID(id),
			whitePlayerId = whitePlayerId,
			blackPlayerId = blackPlayerId,
		)
		persistence.save(game)
		echo("Started game ${game.id.value}")
	}
}
