package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class ResignCommand(
	private val persistence: GameRepository,
) : CliktCommand(name = "resign") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the player who is resigning.").required()
	
	override fun run() {
		val game = persistence.loadById(GameID(gameId))
		val updated = game.resign(game.colorForPlayer(playerId))
		persistence.save(updated)
		echo(updated.formatForCli())
	}
}
