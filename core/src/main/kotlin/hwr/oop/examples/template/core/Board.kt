package hwr.oop.examples.template.core

private const val MIN_RANK = 1
private const val MAX_RANK = 8

class Board {
	companion object {
		fun standardSetup(): Board {
			val board = Board()

			for (file in File.entries) {
				board.place(Pawn(Color.WHITE, Square(file, 2)))
				board.place(Pawn(Color.BLACK, Square(file, 7)))
			}

			placeBackRank(board, Color.WHITE, rank = 1)
			placeBackRank(board, Color.BLACK, rank = 8)

			return board
		}

		private fun placeBackRank(board: Board, color: Color, rank: Int) {
			board.place(Rook(color, Square(File.A, rank)))
			board.place(Knight(color, Square(File.B, rank)))
			board.place(Bishop(color, Square(File.C, rank)))
			board.place(Queen(color, Square(File.D, rank)))
			board.place(King(color, Square(File.E, rank)))
			board.place(Bishop(color, Square(File.F, rank)))
			board.place(Knight(color, Square(File.G, rank)))
			board.place(Rook(color, Square(File.H, rank)))
		}
	}

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
		val targetPiece = pieceAt(move.to)
		require(targetPiece == null || targetPiece.color != piece.color) {
			"Target square is already occupied"
		}

		pieces.remove(move.from)
		pieces[move.to] = piece.moveTo(move.to)
	}
	
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

	fun pieces(color: Color): List<Piece> {
		return pieces.values.filter { it.color == color }
	}
	
	fun validCapture(attacker: Square, target: Square): Boolean {
		val attackingPiece = pieceAt(attacker) ?: return false
		if (pieceAt(target) == null) return false

		return Move(attacker, target) in MovementFactory.availableMoves(attackingPiece, this)
	}
	
}
