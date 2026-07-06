package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class DeclineDrawCommand(
	private val persistence: GameRepository,
) : CliktCommand(name = "declineDraw") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the player declining the draw offer.").required()
	
	override fun run() {
		val game = persistence.loadById(GameID(gameId))
		val playerColor = game.colorForPlayer(playerId)
		if (game.pendingDrawOfferBy == playerColor) {
			throw UsageError("Only the opponent may respond to a draw offer")
		}
		val updated = game.declineDraw()
		persistence.save(updated)
		echo(updated.formatForCli())
	}
}
