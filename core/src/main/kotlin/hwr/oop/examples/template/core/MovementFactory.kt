package hwr.oop.examples.template.core

object MovementFactory {
	private val rookDirections = listOf(
		MovementDirection(Direction.UP),
		MovementDirection(Direction.DOWN),
		MovementDirection(Direction.RIGHT),
		MovementDirection(Direction.LEFT)
	)

	private val bishopDirections = listOf(
		MovementDirection(Direction.UP_RIGHT),
		MovementDirection(Direction.UP_LEFT),
		MovementDirection(Direction.DOWN_RIGHT),
		MovementDirection(Direction.DOWN_LEFT)
	)

	private val kingDirections = listOf(
		MovementDirection(Direction.UP, maxRange = 1),
		MovementDirection(Direction.DOWN, maxRange = 1),
		MovementDirection(Direction.RIGHT, maxRange = 1),
		MovementDirection(Direction.LEFT, maxRange = 1),
		MovementDirection(Direction.UP_RIGHT, maxRange = 1),
		MovementDirection(Direction.UP_LEFT, maxRange = 1),
		MovementDirection(Direction.DOWN_RIGHT, maxRange = 1),
		MovementDirection(Direction.DOWN_LEFT, maxRange = 1)
	)

	private val knightDirections = listOf(
		MovementDirection(Direction.UP_UP_RIGHT, maxRange = 1, canJump = true),
		MovementDirection(Direction.UP_UP_LEFT, maxRange = 1, canJump = true),
		MovementDirection(Direction.DOWN_DOWN_RIGHT, maxRange = 1, canJump = true),
		MovementDirection(Direction.DOWN_DOWN_LEFT, maxRange = 1, canJump = true),
		MovementDirection(Direction.RIGHT_RIGHT_UP, maxRange = 1, canJump = true),
		MovementDirection(Direction.RIGHT_RIGHT_DOWN, maxRange = 1, canJump = true),
		MovementDirection(Direction.LEFT_LEFT_UP, maxRange = 1, canJump = true),
		MovementDirection(Direction.LEFT_LEFT_DOWN, maxRange = 1, canJump = true)
	)

	fun directionsFor(piece: Piece): List<MovementDirection> {
		return when (piece.type) {
			PieceType.PAWN -> pawnDirections(piece.color, piece.hasMoved)
			PieceType.ROOK -> rookDirections
			PieceType.BISHOP -> bishopDirections
			PieceType.KNIGHT -> knightDirections
			PieceType.KING -> kingDirections
			PieceType.QUEEN -> rookDirections + bishopDirections
		}
	}

	fun availableMoves(piece: Piece, board: Board): List<Move> {
		return directionsFor(piece).flatMap { movesInDirection(piece, board, it) }
	}

	fun canCapture(attacker: Piece, target: Piece): Boolean {
		if (attacker.color == target.color) return false

		val fileDistance = target.position.file.ordinal - attacker.position.file.ordinal
		val rankDistance = target.position.rank - attacker.position.rank

		return directionsFor(attacker)
			.filterNot { it.usage == MovementUsage.MOVE_ONLY }
			.any { it.reaches(fileDistance, rankDistance) }
	}

	fun squaresBetween(from: Square, to: Square): List<Square> {
		val fileDistance = to.file.ordinal - from.file.ordinal
		val rankDistance = to.rank - from.rank
		val isStraight = fileDistance == 0 || rankDistance == 0
		val isDiagonal = kotlin.math.abs(fileDistance) == kotlin.math.abs(rankDistance)

		if (!isStraight && !isDiagonal) return emptyList()

		val fileStep = fileDistance.coerceIn(-1, 1)
		val rankStep = rankDistance.coerceIn(-1, 1)
		val result = mutableListOf<Square>()
		var current = from.offset(fileStep, rankStep)

		while (current != null && current != to) {
			result.add(current)
			current = current.offset(fileStep, rankStep)
		}

		return result
	}

	private fun movesInDirection(piece: Piece, board: Board, movement: MovementDirection): List<Move> {
		val moves = mutableListOf<Move>()
		var steps = 1
		var pathIsClear = true

		while (steps <= movement.maxRange && pathIsClear) {
			val target = piece.position.offset(
				movement.fileDelta * steps,
				movement.rankDelta * steps
			)

			if (target == null) {
				pathIsClear = false
			} else {
				val targetPiece = board.pieceAt(target)

				if (targetPiece == null && movement.usage != MovementUsage.CAPTURE_ONLY) {
					moves.add(Move(piece.position, target))
				}

				if (
					targetPiece != null &&
					targetPiece.color != piece.color &&
					movement.usage != MovementUsage.MOVE_ONLY
				) {
					moves.add(Move(piece.position, target))
				}

				if (targetPiece != null && !movement.canJump) {
					pathIsClear = false
				}
			}

			steps++
		}

		return moves
	}

	private fun pawnDirections(color: Color, hasMoved: Boolean): List<MovementDirection> {
		val forwardDirection = if (color == Color.WHITE) Direction.UP else Direction.DOWN
		val leftCaptureDirection = if (color == Color.WHITE) Direction.UP_LEFT else Direction.DOWN_LEFT
		val rightCaptureDirection = if (color == Color.WHITE) Direction.UP_RIGHT else Direction.DOWN_RIGHT
		val forwardRange = if (hasMoved) 1 else 2

		return listOf(
			MovementDirection(forwardDirection, maxRange = forwardRange, usage = MovementUsage.MOVE_ONLY),
			MovementDirection(leftCaptureDirection, maxRange = 1, usage = MovementUsage.CAPTURE_ONLY),
			MovementDirection(rightCaptureDirection, maxRange = 1, usage = MovementUsage.CAPTURE_ONLY),
		)
	}
}
