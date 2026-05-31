package hwr.oop.examples.template.core

private const val MIN_RANK = 1
private const val MAX_RANK = 8

class Board {
	private val squares: Map<Pair<File, Int>, Square> =
		buildMap {
			for (file in File.entries) {
				for (rank in MIN_RANK..MAX_RANK) {
					val key = file to rank
					val square = Square(file, rank)
					
					put(key, square)
				}
			}
		}
	
	private val pieces: MutableMap<Square, Piece> = mutableMapOf()
	
	fun squareAt(file: File, rank: Int): Square {
		require(rank in MIN_RANK..MAX_RANK) { "Rank must be between $MIN_RANK and $MAX_RANK" }
		
		return squares.getValue(file to rank)
	}
	
	fun place(piece: Piece) {
		require(pieceAt(piece.position) == null) { "Square is already occupied" }
		pieces[piece.position] = piece
	}
	
	fun applyMove(move: Move) {
		val piece = pieceAt(move.from)
		require(piece != null) { "No piece on start square" }
		require(pieceAt(move.to) == null) { "Target square is already occupied" }
		
		pieces.remove(move.from)
		pieces[move.to] = piece.moveTo(move.to)
	}
	
	// for Debugging: Shows the board as 8 text rows, starting with rank 8.
	fun showBoard(): String {
		val rows = mutableListOf<String>()
		
		for (rank in MAX_RANK downTo MIN_RANK) {
			var row = ""
			
			for (file in File.entries) {
				val piece = pieceAt(Square(file, rank))
				row += piece?.symbol() ?: " "
			}
			
			rows.add(row)
		}
		
		return rows.joinToString("\n")
	}
	
	fun pieceAt(square: Square): Piece? {
		return pieces[square]
	}
	
	fun validCapture(attacker: Square, target: Square): Boolean {
		val attackingPiece = pieceAt(attacker) ?: return false
		val targetPiece = pieceAt(target) ?: return false
		
		if (!attackingPiece.canCapture(targetPiece)) return false
		if (attackingPiece.directions().any { it.canJump }) return true
		
		return squaresBetween(attacker, target).all { pieceAt(it) == null }
	}
	
	fun squaresBetween(attacker: Square, target: Square): List<Square> {
		val fileDistance = target.file.ordinal - attacker.file.ordinal
		val rankDistance = target.rank - attacker.rank
		
		var fileStep = 0
		var rankStep = 0
		
		if (fileDistance > 0) {
			fileStep = 1
		} else if (fileDistance < 0) {
			fileStep = -1
		}
		
		if (rankDistance > 0) {
			rankStep = 1
		} else if (rankDistance < 0) {
			rankStep = -1
		}
		
		val result = mutableListOf<Square>()
		var current = attacker.offset(fileStep, rankStep)
		
		while (current != null && current != target) {
			result.add(current)
			current = current.offset(fileStep, rankStep)
		}
		
		return result
	}
	
}
