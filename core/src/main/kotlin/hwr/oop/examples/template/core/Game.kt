package hwr.oop.examples.template.core

class Game(
	val id: GameID,
	val board: Board = Board.standardSetup(),
	val turn: Turn = Turn(1, Color.WHITE),
	val status: GameStatus = GameStatus.ONGOING,
	val positionStatus: PositionStatus = PositionStatus.NORMAL,
	val result: GameResult? = null,
	val pendingDrawOfferBy: Color? = null,
) {
	init {
		if (status == GameStatus.ONGOING) {
			require(result == null) { "An ongoing game cannot have a result" }
		}

		if (status == GameStatus.FINISHED) {
			require(result != null) { "A finished game must have a result" }
			require(pendingDrawOfferBy == null) { "A finished game cannot have a pending draw offer" }
		}
	}

	fun availableMoves(): List<Move> {
		if (status == GameStatus.FINISHED) return emptyList()

		return board.pieces(turn.color)
			.flatMap { MovementFactory.availableMoves(it, board) }
			.filter { !leavesOwnKingInCheck(it) }
	}

	fun makeMove(move: Move): Game {
		require(status == GameStatus.ONGOING) { "Game is not in progress" }
		require(move in availableMoves()) { "Move is not available" }

		board.applyMove(move)
		val nextTurn = turn.next()
		val nextKingSquare = board.kingSquare(nextTurn.color)
		val nextPositionStatus = when {
			nextKingSquare == null -> PositionStatus.NORMAL
			board.isAttackedBy(nextKingSquare, turn.color) -> PositionStatus.CHECK
			else -> PositionStatus.NORMAL
		}

		return Game(
			id = id,
			board = board,
			turn = nextTurn,
			status = status,
			positionStatus = nextPositionStatus,
			result = result,
			pendingDrawOfferBy = pendingDrawOfferBy,
		)
	}

	private fun leavesOwnKingInCheck(move: Move): Boolean {
		val probe = board.copy()
		probe.applyMove(move)
		val ownKing = probe.kingSquare(turn.color) ?: return false
		return probe.isAttackedBy(ownKing, turn.color.opposite())
	}

	fun showBoard(): String {
		return "Turn ${turn.number}:\n\n${board.showBoard()}"
	}
}
