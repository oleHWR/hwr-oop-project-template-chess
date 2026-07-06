package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository

class GetAvailableMovesCommand(
	private val persistence: GameRepository,
) : CliktCommand(name = "getAvailableMoves") {
	private val gameId by requireObject<String>()
	
	override fun run() {
		val game = persistence.loadById(GameID(gameId))
		val moves = game.availableMoves()
			.sortedWith(compareBy({ it.from.file.ordinal }, { it.from.rank }, { it.to.file.ordinal }, { it.to.rank }))
			.map { "${it.from.formatForCli()}-${it.to.formatForCli()}" }
		echo(if (moves.isEmpty()) "No available moves" else moves.joinToString("\n"))
	}
}
