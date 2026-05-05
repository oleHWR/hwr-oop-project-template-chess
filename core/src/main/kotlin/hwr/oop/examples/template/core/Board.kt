package hwr.oop.examples.template.core

class Board {
    private val pieces: MutableList<Piece> = mutableListOf()

    fun squareAt(file: File, rank: Int): Square {
        return Square(file, rank)
    }

    fun place(piece: Piece) {
        require(pieceAt(piece.position) == null) { "Square is already occupied" }
        pieces.add(piece)
    }

    fun applyMove(move: Move) {
        val piece = pieceAt(move.from)
        require(piece != null) { "No piece on start square" }
        require(pieceAt(move.to) == null) { "Target square is already occupied" }

        pieces.remove(piece)
        pieces.add(piece.moveTo(move.to))
    }

    fun pieceAt(square: Square): Piece? {
        for (piece in pieces) {
            if (piece.position == square) {
                return piece
            }
        }
        return null
    }
}
