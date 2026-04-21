package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class StartGameCommand : CliktCommand(name = "startGame") {
	private val whitePlayerId by option(
		"--white-player-id",
		help = "The player ID of the player who will control the white pieces."
	).required()
	private val blackPlayerId by option(
		"--black-player-id",
		help = "The player ID of the player who will control the black pieces."
	).required()
	
	override fun run(): Unit = TODO()
}
