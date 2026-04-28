package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RookTest {

    @Test
    fun `rook has exactly 4 movement patterns`() {
        // given
        val rook = Rook(Color.WHITE, Square(File.A, 1))
        // when
        val patterns = rook.movementPatterns()
        // then
        assertThat(patterns).hasSize(4)
    }

    @Test
    fun `all rook patterns have maxRange 7`() {
        // given
        val rook = Rook(Color.WHITE, Square(File.A, 1))
        // when
        val patterns = rook.movementPatterns()
        // then
        assertThat(patterns).allMatch { it.maxRange == 7 }
    }

    @Test
    fun `no rook pattern has canJump`() {
        // given
        val rook = Rook(Color.WHITE, Square(File.A, 1))
        // when
        val patterns = rook.movementPatterns()
        // then
        assertThat(patterns).noneMatch { it.canJump }
    }

    @Test
    fun `no rook pattern has captureOnly or moveOnly`() {
        // given
        val rook = Rook(Color.WHITE, Square(File.A, 1))
        // when
        val patterns = rook.movementPatterns()
        // then
        assertThat(patterns).noneMatch { it.captureOnly || it.moveOnly }
    }

    @Test
    fun `rook covers 4 cardinal directions`() {
        // given
        val rook = Rook(Color.WHITE, Square(File.A, 1))
        // when
        val deltas = rook.movementPatterns().map { it.fileDelta to it.rankDelta }.toSet()
        // then
        assertThat(deltas).containsExactlyInAnyOrder(
            0 to 1, 0 to -1, 1 to 0, -1 to 0
        )
    }

    @Test
    fun `moveTo returns new rook with updated position and hasMoved true`() {
        // given
        val rook = Rook(Color.WHITE, Square(File.A, 1))
        val target = Square(File.A, 5)
        // when
        val moved = rook.moveTo(target)
        // then
        assertThat(moved.position).isEqualTo(target)
        assertThat(moved.hasMoved).isTrue()
        assertThat(moved.color).isEqualTo(Color.WHITE)
    }

    @Test
    fun `rook default hasMoved is false`() {
        // given / when
        val rook = Rook(Color.BLACK, Square(File.H, 8))
        // then
        assertThat(rook.hasMoved).isFalse()
    }
}
