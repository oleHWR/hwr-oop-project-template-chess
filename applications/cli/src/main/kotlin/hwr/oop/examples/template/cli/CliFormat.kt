package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.UsageError
import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameEndReason
import hwr.oop.examples.template.core.GameStatus
import hwr.oop.examples.template.core.Square

fun Game.colorForPlayer(playerId: String): Color {
	return when (playerId) {
		whitePlayerId -> Color.WHITE
		blackPlayerId -> Color.BLACK
		else -> throw UsageError("Unknown player ID: $playerId")
	}
}

fun Game.formatForCli(): String {
	val gameResult = result
	val drawOfferBy = pendingDrawOfferBy
	val lines = mutableListOf(
		"Game: ${id.value}",
		"Status: ${status.formatForCli(gameResult?.reason)}",
		"Turn: ${turn.color} ${turn.number}",
		"White: $whitePlayerId",
		"Black: $blackPlayerId",
	)
	if (positionStatus != hwr.oop.examples.template.core.PositionStatus.NORMAL) {
		lines.add("Position: $positionStatus")
	}
	if (drawOfferBy != null) {
		lines.add("Draw offer by: ${playerIdFor(drawOfferBy)}")
	}
	if (gameResult != null) {
		lines.add("Result: ${gameResult.reason}${gameResult.winner?.let { ", winner ${playerIdFor(it)}" } ?: ""}")
	}
	lines.add("")
	lines.add(showBoard())
	return lines.joinToString("\n")
}

fun Square.formatForCli(): String {
	return "${file.name}$rank"
}

fun String.parseSquare(): Square {
	val normalized = trim().uppercase()
	if (normalized.length != 2) {
		throw UsageError("Square must look like E2")
	}
	val file = File.entries.firstOrNull { it.name == normalized[0].toString() }
		?: throw UsageError("Square file must be between A and H")
	val rank = normalized[1].digitToIntOrNull()
		?: throw UsageError("Square rank must be between 1 and 8")
	return try {
		Square(file, rank)
	} catch (e: IllegalArgumentException) {
		throw UsageError("Square rank must be between 1 and 8")
	}
}

private fun Game.playerIdFor(color: Color): String {
	return when (color) {
		Color.WHITE -> whitePlayerId
		Color.BLACK -> blackPlayerId
	}
}

private fun GameStatus.formatForCli(reason: GameEndReason?): String {
	return if (reason == null) name else "$name ($reason)"
}
