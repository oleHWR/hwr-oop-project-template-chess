package hwr.oop.examples.template.core

class Game(
	val id: GameID,
	val board: Board = Board(),
	val turn: Turn = Turn(1, Color.WHITE),
	val status: GameStatus = GameStatus.ONGOING,
) {
	// for Debugging: Shows the current turn and board as text.
	fun showBoard(): String {
		return "Turn ${turn.number}:\n\n${board.showBoard()}"
	}
}
