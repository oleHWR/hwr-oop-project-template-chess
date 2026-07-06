package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository

class GetGameCommand(
	private val persistence: GameRepository,
) : CliktCommand(name = "getGame") {
	private val gameId by requireObject<String>()
	
	override fun run() {
		val game = persistence.loadById(GameID(gameId))
		echo(game.formatForCli())
	}
}
