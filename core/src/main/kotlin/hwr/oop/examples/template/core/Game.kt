package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
class Game(
	val id: GameID,
	val board: Board = Board.standardSetup(),
	val turn: Turn = Turn(1, Color.WHITE),
	val status: GameStatus = GameStatus.ONGOING,
	val positionStatus: PositionStatus = PositionStatus.NORMAL,
	val result: GameResult? = null,
	val pendingDrawOfferBy: Color? = null,
	val whitePlayerId: String = "WHITE",
	val blackPlayerId: String = "BLACK",
	val halfmoveClock: Int = 0,
) {
	init {
		require(whitePlayerId.isNotBlank()) { "White player ID must not be blank" }
		require(blackPlayerId.isNotBlank()) { "Black player ID must not be blank" }
		require(whitePlayerId != blackPlayerId) { "Players must be different" }
		require(halfmoveClock >= 0) { "Halfmove clock must not be negative" }

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

		val movingPiece = board.pieceAt(move.from)
		val captured = board.pieceAt(move.to) != null
		val isPawnMove = movingPiece?.type == PieceType.PAWN
		val nextHalfmoveClock = if (isPawnMove || captured) 0 else halfmoveClock + 1

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
			whitePlayerId = whitePlayerId,
			blackPlayerId = blackPlayerId,
			halfmoveClock = nextHalfmoveClock,
		)

		if (ongoing.availableMoves().isEmpty()) {
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
				whitePlayerId = whitePlayerId,
				blackPlayerId = blackPlayerId,
				halfmoveClock = nextHalfmoveClock,
			)
		}

		if (nextHalfmoveClock >= 100) {
			return Game(
				id = id,
				board = board,
				turn = nextTurn,
				status = GameStatus.FINISHED,
				positionStatus = nextPositionStatus,
				result = GameResult(GameEndReason.FIFTY_MOVE_RULE),
				whitePlayerId = whitePlayerId,
				blackPlayerId = blackPlayerId,
				halfmoveClock = nextHalfmoveClock,
			)
		}

		return ongoing
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
			whitePlayerId = whitePlayerId,
			blackPlayerId = blackPlayerId,
			halfmoveClock = halfmoveClock,
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
			whitePlayerId = whitePlayerId,
			blackPlayerId = blackPlayerId,
			halfmoveClock = halfmoveClock,
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
			whitePlayerId = whitePlayerId,
			blackPlayerId = blackPlayerId,
			halfmoveClock = halfmoveClock,
		)
	}

	fun resign(by: Color): Game {
		require(status == GameStatus.ONGOING) { "Game is not in progress" }

		return Game(
			id = id,
			board = board,
			turn = turn,
			status = GameStatus.FINISHED,
			positionStatus = positionStatus,
			result = GameResult(GameEndReason.RESIGNED, by.opposite()),
			pendingDrawOfferBy = null,
			whitePlayerId = whitePlayerId,
			blackPlayerId = blackPlayerId,
			halfmoveClock = halfmoveClock,
		)
	}

	fun showBoard(): String {
		return "Turn ${turn.number}:\n\n${board.showBoard()}"
	}
}
