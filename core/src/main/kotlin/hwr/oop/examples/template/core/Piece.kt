package hwr.oop.examples.template.core

// Represents one chess piece with its type, color, position, and move state.
class Piece(
    val type: PieceType,
    val color: Color,
    val position: Square,
    val hasMoved: Boolean = false
) {

    // Returns the movement patterns this piece type can use.
    fun movementPatterns(): List<MovementPattern> {
        return when (type) {
            PieceType.KING -> listOf(
                MovementPattern(0, 1, 1),
                MovementPattern(0, -1, 1),
                MovementPattern(1, 0, 1),
                MovementPattern(-1, 0, 1),
                MovementPattern(1, 1, 1),
                MovementPattern(-1, 1, 1),
                MovementPattern(1, -1, 1),
                MovementPattern(-1, -1, 1)
            )

            PieceType.ROOK -> listOf(
                MovementPattern(0, 1, 7),
                MovementPattern(0, -1, 7),
                MovementPattern(1, 0, 7),
                MovementPattern(-1, 0, 7)
            )

            else -> emptyList()
        }
    }

    // Creates a new version of this piece on the target square.
    fun moveTo(target: Square): Piece {
        return Piece(type, color, target, true)
    }
}
