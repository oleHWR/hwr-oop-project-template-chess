package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class AcceptDrawCommand : CliktCommand(name = "acceptDraw") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the player accepting the draw offer.").required()
	
	override fun run(): Unit = TODO()
}
