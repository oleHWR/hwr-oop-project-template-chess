package hwr.oop.examples.template.core

private const val MIN_RANK = 1
private const val MAX_RANK = 8

// Represents the chessboard and keeps track of its squares and pieces.
class Board {
	// Stores all 64 squares by file and rank.
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
	
	// Stores the pieces that are currently on the board by their square.
	private val pieces: MutableMap<Square, Piece> = mutableMapOf()
	
	// Returns the square at a given file and rank.
	fun squareAt(file: File, rank: Int): Square {
		require(rank in MIN_RANK..MAX_RANK) { "Rank must be between $MIN_RANK and $MAX_RANK" }
		
		return squares.getValue(file to rank)
	}
	
	// Adds a piece to the board if its square is still empty.
	fun place(piece: Piece) {
		require(pieceAt(piece.position) == null) { "Square is already occupied" }
		pieces[piece.position] = piece
	}
	
	// Applies a move by replacing the old piece with its moved version.
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
	
	// Finds the piece on a square, or returns null if the square is empty.
	fun pieceAt(square: Square): Piece? {
		return pieces[square]
	}
}
