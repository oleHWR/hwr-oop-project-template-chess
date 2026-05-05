package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PieceTest {

    @Test
    fun `rook has four movement patterns`() {
        // given
        val rook = Piece(PieceType.ROOK, Color.WHITE, Square(File.A, 1))

        // when
        val patterns = rook.movementPatterns()

        // then
        assertThat(patterns).hasSize(4)
    }

    @Test
    fun `rook can move horizontally and vertically`() {
        // given
        val rook = Piece(PieceType.ROOK, Color.WHITE, Square(File.A, 1))

        // when
        val patterns = rook.movementPatterns()

        // then
        assertThat(patterns[0].fileDelta).isEqualTo(0)
        assertThat(patterns[0].rankDelta).isEqualTo(1)
        assertThat(patterns[1].fileDelta).isEqualTo(0)
        assertThat(patterns[1].rankDelta).isEqualTo(-1)
        assertThat(patterns[2].fileDelta).isEqualTo(1)
        assertThat(patterns[2].rankDelta).isEqualTo(0)
        assertThat(patterns[3].fileDelta).isEqualTo(-1)
        assertThat(patterns[3].rankDelta).isEqualTo(0)
    }

    @Test
    fun `king has eight movement patterns`() {
        // given
        val king = Piece(PieceType.KING, Color.WHITE, Square(File.E, 1))

        // when
        val patterns = king.movementPatterns()

        // then
        assertThat(patterns).hasSize(8)
    }

    @Test
    fun `king only moves one square`() {
        // given
        val king = Piece(PieceType.KING, Color.WHITE, Square(File.E, 1))

        // when
        val patterns = king.movementPatterns()

        // then
        assertThat(patterns[0].maxRange).isEqualTo(1)
        assertThat(patterns[1].maxRange).isEqualTo(1)
        assertThat(patterns[2].maxRange).isEqualTo(1)
        assertThat(patterns[3].maxRange).isEqualTo(1)
        assertThat(patterns[4].maxRange).isEqualTo(1)
        assertThat(patterns[5].maxRange).isEqualTo(1)
        assertThat(patterns[6].maxRange).isEqualTo(1)
        assertThat(patterns[7].maxRange).isEqualTo(1)
    }

    @Test
    fun `queen has no movement patterns yet`() {
        // given
        val queen = Piece(PieceType.QUEEN, Color.WHITE, Square(File.D, 1))

        // when
        val patterns = queen.movementPatterns()

        // then
        assertThat(patterns).isEmpty()
    }

    @Test
    fun `moveTo creates piece with new position`() {
        // given
        val rook = Piece(PieceType.ROOK, Color.WHITE, Square(File.A, 1))
        val target = Square(File.A, 5)

        // when
        val moved = rook.moveTo(target)

        // then
        assertThat(moved.position).isEqualTo(target)
        assertThat(moved.type).isEqualTo(PieceType.ROOK)
        assertThat(moved.color).isEqualTo(Color.WHITE)
        assertThat(moved.hasMoved).isTrue()
    }

    @Test
    fun `new piece has not moved`() {
        // given / when
        val rook = Piece(PieceType.ROOK, Color.BLACK, Square(File.H, 8))

        // then
        assertThat(rook.hasMoved).isFalse()
    }
}
