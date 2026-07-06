package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.Move
import hwr.oop.examples.template.ports.out.GameRepository
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class MakeMoveCommand(
	private val persistence: GameRepository,
) : CliktCommand(name = "makeMove") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the player making the move.").required()
	private val from by option("--from", help = "The source square of the piece to move (e.g. E2).").required()
	private val to by option("--to", help = "The target square to move the piece to (e.g. E4).").required()
	private val promotionPiece by option(
		"--promotion-piece",
		help = "Required when a pawn reaches the last rank. Possible values: QUEEN, ROOK, BISHOP, KNIGHT."
	)
	
	override fun run() {
		if (promotionPiece != null) {
			throw UsageError("Pawn promotion is not implemented yet")
		}
		val game = persistence.loadById(GameID(gameId))
		val playerColor = game.colorForPlayer(playerId)
		if (playerColor != game.turn.color) {
			throw UsageError("It is ${game.turn.color}'s turn")
		}
		val updated = game.makeMove(Move(from.parseSquare(), to.parseSquare()))
		persistence.save(updated)
		echo(updated.formatForCli())
	}
}
