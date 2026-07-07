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
	val enPassantTarget: Square? = null,
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

		val standard = board.pieces(turn.color)
			.flatMap { MovementFactory.availableMoves(it, board) }
		val enPassant = enPassantMoves()
		val castles = castlingMoves()

		return (standard + enPassant + castles).filter { !leavesOwnKingInCheck(it) }
	}

	private fun enPassantMoves(): List<Move> {
		val target = enPassantTarget ?: return emptyList()
		val victimRank = if (turn.color == Color.WHITE) target.rank - 1 else target.rank + 1
		val victim = board.pieceAt(Square(target.file, victimRank))
		if (victim?.type != PieceType.PAWN || victim.color == turn.color) return emptyList()

		val candidateFiles = listOf(target.file.ordinal - 1, target.file.ordinal + 1)
			.filter { it in 0..7 }
			.map { File.entries[it] }

		return candidateFiles.mapNotNull { file ->
			val from = Square(file, victimRank)
			val piece = board.pieceAt(from)
			if (piece?.type == PieceType.PAWN && piece.color == turn.color) {
				Move(from, target)
			} else {
				null
			}
		}
	}

	private fun castlingMoves(): List<Move> {
		val rank = if (turn.color == Color.WHITE) 1 else 8
		val kingSquare = Square(File.E, rank)
		val king = board.pieceAt(kingSquare)
		if (king?.type != PieceType.KING || king.color != turn.color || king.hasMoved) return emptyList()
		if (board.isAttackedBy(kingSquare, turn.color.opposite())) return emptyList()

		val moves = mutableListOf<Move>()

		val kingsideRook = board.pieceAt(Square(File.H, rank))
		if (
			kingsideRook?.type == PieceType.ROOK &&
			kingsideRook.color == turn.color &&
			!kingsideRook.hasMoved &&
			board.pieceAt(Square(File.F, rank)) == null &&
			board.pieceAt(Square(File.G, rank)) == null &&
			!board.isAttackedBy(Square(File.F, rank), turn.color.opposite()) &&
			!board.isAttackedBy(Square(File.G, rank), turn.color.opposite())
		) {
			moves.add(Move(kingSquare, Square(File.G, rank)))
		}

		val queensideRook = board.pieceAt(Square(File.A, rank))
		if (
			queensideRook?.type == PieceType.ROOK &&
			queensideRook.color == turn.color &&
			!queensideRook.hasMoved &&
			board.pieceAt(Square(File.B, rank)) == null &&
			board.pieceAt(Square(File.C, rank)) == null &&
			board.pieceAt(Square(File.D, rank)) == null &&
			!board.isAttackedBy(Square(File.D, rank), turn.color.opposite()) &&
			!board.isAttackedBy(Square(File.C, rank), turn.color.opposite())
		) {
			moves.add(Move(kingSquare, Square(File.C, rank)))
		}

		return moves
	}

	fun makeMove(move: Move): Game {
		require(status == GameStatus.ONGOING) { "Game is not in progress" }
		require(move in availableMoves()) { "Move is not available" }

		val movingPiece = board.pieceAt(move.from)
		val isEnPassant = isEnPassantMove(move, movingPiece, board)
		val isCastling = isCastlingMove(move, movingPiece)
		val captured = board.pieceAt(move.to) != null || isEnPassant
		val isPawnMove = movingPiece?.type == PieceType.PAWN
		val nextHalfmoveClock = if (isPawnMove || captured) 0 else halfmoveClock + 1

		applyMoveOn(board, move, isEnPassant, isCastling)

		val nextEnPassantTarget = computeEnPassantTarget(movingPiece, move)
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
			enPassantTarget = nextEnPassantTarget,
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
				enPassantTarget = nextEnPassantTarget,
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
				enPassantTarget = nextEnPassantTarget,
			)
		}

		return ongoing
	}

	private fun applyMoveOn(target: Board, move: Move, isEnPassant: Boolean, isCastling: Boolean) {
		if (isEnPassant) {
			val capturedRank = if (turn.color == Color.WHITE) move.to.rank - 1 else move.to.rank + 1
			target.remove(Square(move.to.file, capturedRank))
		}
		target.applyMove(move)
		if (isCastling) {
			val rank = move.to.rank
			val (rookFrom, rookTo) = if (move.to.file == File.G) {
				Square(File.H, rank) to Square(File.F, rank)
			} else {
				Square(File.A, rank) to Square(File.D, rank)
			}
			target.applyMove(Move(rookFrom, rookTo))
		}
	}

	private fun computeEnPassantTarget(piece: Piece?, move: Move): Square? {
		if (piece?.type != PieceType.PAWN) return null
		val rankDiff = move.to.rank - move.from.rank
		if (kotlin.math.abs(rankDiff) != 2) return null
		val skippedRank = (move.from.rank + move.to.rank) / 2
		return Square(move.from.file, skippedRank)
	}

	private fun leavesOwnKingInCheck(move: Move): Boolean {
		val probe = board.copy()
		val movingPiece = probe.pieceAt(move.from)
		val isEnPassant = isEnPassantMove(move, movingPiece, probe)
		val isCastling = isCastlingMove(move, movingPiece)
		applyMoveOn(probe, move, isEnPassant, isCastling)
		val ownKing = probe.kingSquare(turn.color) ?: return false
		return probe.isAttackedBy(ownKing, turn.color.opposite())
	}

	private fun isEnPassantMove(move: Move, piece: Piece?, boardView: Board): Boolean {
		if (piece?.type != PieceType.PAWN) return false
		if (move.to != enPassantTarget) return false
		if (move.from.file == move.to.file) return false
		return boardView.pieceAt(move.to) == null
	}

	private fun isCastlingMove(move: Move, piece: Piece?): Boolean {
		if (piece?.type != PieceType.KING) return false
		return kotlin.math.abs(move.to.file.ordinal - move.from.file.ordinal) == 2
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
			enPassantTarget = enPassantTarget,
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
			enPassantTarget = enPassantTarget,
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
			enPassantTarget = enPassantTarget,
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
			enPassantTarget = enPassantTarget,
		)
	}

	fun showBoard(): String {
		return "Turn ${turn.number}:\n\n${board.showBoard()}"
	}
}
