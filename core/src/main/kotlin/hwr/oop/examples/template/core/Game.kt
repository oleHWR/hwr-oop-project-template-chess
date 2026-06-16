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

		val ongoing = Game(
			id = id,
			board = board,
			turn = nextTurn,
			status = GameStatus.ONGOING,
			positionStatus = nextPositionStatus,
			pendingDrawOfferBy = pendingDrawOfferBy,
		)

		if (ongoing.availableMoves().isNotEmpty()) return ongoing

		val endReason = if (nextPositionStatus == PositionStatus.CHECK) {
			GameEndReason.CHECKMATE
		} else {
			GameEndReason.STALEMATE
		}
		val winner = if (endReason == GameEndReason.CHECKMATE) turn.color else null

		return Game(
			id = id,
			board = board,
			turn = nextTurn,
			status = GameStatus.FINISHED,
			positionStatus = nextPositionStatus,
			result = GameResult(endReason, winner),
		)
	}

	private fun leavesOwnKingInCheck(move: Move): Boolean {
		val probe = board.copy()
		probe.applyMove(move)
		val ownKing = probe.kingSquare(turn.color) ?: return false
		return probe.isAttackedBy(ownKing, turn.color.opposite())
	}

	fun offerDraw(by: Color): Game {
		require(status == GameStatus.ONGOING) { "Game is not in progress" }
		require(pendingDrawOfferBy == null) { "A draw offer is already pending" }
		require(by == turn.color) { "Only the side to move may offer a draw" }

		return Game(
			id = id,
			board = board,
			turn = turn,
			status = status,
			positionStatus = positionStatus,
			result = result,
			pendingDrawOfferBy = by,
		)
	}

	fun declineDraw(): Game {
		require(pendingDrawOfferBy != null) { "No draw offer to respond to" }

		return Game(
			id = id,
			board = board,
			turn = turn,
			status = status,
			positionStatus = positionStatus,
			result = result,
			pendingDrawOfferBy = null,
		)
	}

	fun acceptDraw(): Game {
		require(pendingDrawOfferBy != null) { "No draw offer to respond to" }

		return Game(
			id = id,
			board = board,
			turn = turn,
			status = GameStatus.FINISHED,
			positionStatus = positionStatus,
			result = GameResult(GameEndReason.DRAW_ACCEPTED),
			pendingDrawOfferBy = null,
		)
	}

	fun showBoard(): String {
		return "Turn ${turn.number}:\n\n${board.showBoard()}"
	}
}
