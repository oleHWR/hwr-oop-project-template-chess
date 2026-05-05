package hwr.oop.examples.template.core

sealed class Piece {
    abstract val color: Color
    abstract val position: Square
    abstract val hasMoved: Boolean

    abstract fun movementPatterns(): List<MovementPattern>
    abstract fun moveTo(target: Square): Piece
}