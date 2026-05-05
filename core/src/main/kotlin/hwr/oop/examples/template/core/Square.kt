package hwr.oop.examples.template.core

// Represents one square on the chessboard, for example A1 or E4.
class Square(
    val file: File,
    val rank: Int,
    val color: Color = squareColor(file, rank)
) {
    // Makes sure the square is on the board.
    init {
        require(rank in 1..8)
    }

    // Creates a new square by moving from this square. Returns null if the move leaves the board.
    fun offset(fileDelta: Int, rankDelta: Int): Square? {
        val newFileOrdinal = file.ordinal + fileDelta
        val newRank = rank + rankDelta
        if (newFileOrdinal !in 0..7 || newRank !in 1..8) return null
        return Square(File.entries[newFileOrdinal], newRank)
    }

    // Lets two Square objects count as equal when they have the same file and rank.
    override fun equals(other: Any?): Boolean {
        if (other !is Square) return false
        return file == other.file && rank == other.rank
    }

    // Needed to make sure equals works correctly.
    override fun hashCode(): Int {
        return file.hashCode() + rank
    }

    // Displays the square in chess notation, for example A1 or E4.
    override fun toString(): String {
        return "$file$rank"
    }

    // Companion object so squareColor is not a loose function.
    companion object {
        // Calculates the board color for a square from its file and rank.
        private fun squareColor(file: File, rank: Int): Color {
            return if ((file.ordinal + rank - 1) % 2 == 0) Color.BLACK else Color.WHITE
        }
    }
}
