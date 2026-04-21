package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class MakeMoveCommand : CliktCommand(name = "makeMove") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the player making the move.").required()
	private val from by option("--from", help = "The source square of the piece to move (e.g. E2).").required()
	private val to by option("--to", help = "The target square to move the piece to (e.g. E4).").required()
	private val promotionPiece by option(
		"--promotion-piece",
		help = "Required when a pawn reaches the last rank. Possible values: QUEEN, ROOK, BISHOP, KNIGHT."
	)
	
	override fun run(): Unit = TODO()
}
