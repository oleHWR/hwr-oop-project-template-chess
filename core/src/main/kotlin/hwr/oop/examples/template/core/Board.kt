package hwr.oop.examples.template.core

// Represents the chessboard and keeps track of its squares and pieces.
class Board {
    // Stores all 64 squares, grouped by rank and file.
    private val squares: List<List<Square>> = List(8) { rankIndex ->
        List(8) { fileIndex ->
            Square(file = File.entries[fileIndex], rank = rankIndex + 1)
        }
    }

    // Stores the pieces that are currently on the board.
    private val pieces: MutableList<Piece> = mutableListOf()

    // Returns the square at a given file and rank.
    fun squareAt(file: File, rank: Int): Square {
        return squares[rank - 1][file.ordinal]
    }

    // Adds a piece to the board if its square is still empty.
    fun place(piece: Piece) {
        require(pieceAt(piece.position) == null) { "Square is already occupied" }
        pieces.add(piece)
    }

    // Applies a move by replacing the old piece with its moved version.
    fun applyMove(move: Move) {
        val piece = pieceAt(move.from)
        require(piece != null) { "No piece on start square" }
        require(pieceAt(move.to) == null) { "Target square is already occupied" }

        pieces.remove(piece)
        pieces.add(piece.moveTo(move.to))
    }

    // Finds the piece on a square, or returns null if the square is empty.
    fun pieceAt(square: Square): Piece? {
        for (piece in pieces) {
            if (piece.position == square) {
                return piece
            }
        }
        return null
    }
}
