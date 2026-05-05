package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QueenTest {

    @Test
    fun `queen has exactly 8 movement patterns`() {
        // given
        val queen = Queen(Color.WHITE, Square(File.D, 1))
        // when
        val patterns = queen.movementPatterns()
        // then
        assertThat(patterns).hasSize(8)
    }

    @Test
    fun `all queen patterns have maxRange 7`() {
        // given
        val queen = Queen(Color.WHITE, Square(File.D, 1))
        // when
        val patterns = queen.movementPatterns()
        // then
        assertThat(patterns).allMatch { it.maxRange == 7 }
    }

    @Test
    fun `no queen pattern has canJump`() {
        // given
        val queen = Queen(Color.WHITE, Square(File.D, 1))
        // when
        val patterns = queen.movementPatterns()
        // then
        assertThat(patterns).noneMatch { it.canJump }
    }

    @Test
    fun `queen covers all 4 cardinal directions`() {
        // given
        val queen = Queen(Color.WHITE, Square(File.D, 1))
        // when
        val deltas = queen.movementPatterns().map { it.fileDelta to it.rankDelta }.toSet()
        // then
        assertThat(deltas).contains(
            0 to 1, 0 to -1, 1 to 0, -1 to 0
        )
    }

    @Test
    fun `queen covers all 4 diagonal directions`() {
        // given
        val queen = Queen(Color.WHITE, Square(File.D, 1))
        // when
        val deltas = queen.movementPatterns().map { it.fileDelta to it.rankDelta }.toSet()
        // then
        assertThat(deltas).contains(
            1 to 1, -1 to 1, 1 to -1, -1 to -1
        )
    }

    @Test
    fun `moveTo returns new queen with updated position and hasMoved true`() {
        // given
        val queen = Queen(Color.WHITE, Square(File.D, 1))
        val target = Square(File.D, 5)
        // when
        val moved = queen.moveTo(target)
        // then
        assertThat(moved.position).isEqualTo(target)
        assertThat(moved.hasMoved).isTrue()
        assertThat(moved.color).isEqualTo(Color.WHITE)
    }

    @Test
    fun `queen default hasMoved is false`() {
        // given / when
        val queen = Queen(Color.BLACK, Square(File.D, 8))
        // then
        assertThat(queen.hasMoved).isFalse()
    }
}
