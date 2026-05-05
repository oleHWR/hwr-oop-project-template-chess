package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KingTest {

    @Test
    fun `king has exactly 8 movement patterns`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1))
        // when
        val patterns = king.movementPatterns()
        // then
        assertThat(patterns).hasSize(8)
    }

    @Test
    fun `all king patterns have maxRange 1`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1))
        // when
        val patterns = king.movementPatterns()
        // then
        assertThat(patterns).allMatch { it.maxRange == 1 }
    }

    @Test
    fun `no king pattern has canJump`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1))
        // when
        val patterns = king.movementPatterns()
        // then
        assertThat(patterns).noneMatch { it.canJump }
    }

    @Test
    fun `king covers all 8 directions`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1))
        // when
        val deltas = king.movementPatterns().map { it.fileDelta to it.rankDelta }.toSet()
        // then
        assertThat(deltas).containsExactlyInAnyOrder(
            0 to 1, 0 to -1, 1 to 0, -1 to 0,
            1 to 1, -1 to 1, 1 to -1, -1 to -1
        )
    }

    @Test
    fun `moveTo creates new king with updated position`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1))
        val target = Square(File.E, 2)
        // when
        val moved = king.moveTo(target)
        // then
        assertThat(moved.position).isEqualTo(target)
        assertThat(moved.color).isEqualTo(Color.WHITE)
    }

    @Test
    fun `moveTo sets hasMoved to true`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1), hasMoved = false)
        // when
        val moved = king.moveTo(Square(File.E, 2))
        // then
        assertThat(moved.hasMoved).isTrue()
    }

    @Test
    fun `original king unchanged after moveTo`() {
        // given
        val king = King(Color.WHITE, Square(File.E, 1))
        // when
        king.moveTo(Square(File.E, 2))
        // then
        assertThat(king.position).isEqualTo(Square(File.E, 1))
        assertThat(king.hasMoved).isFalse()
    }

    @Test
    fun `king default hasMoved is false`() {
        // given / when
        val king = King(Color.BLACK, Square(File.E, 8))
        // then
        assertThat(king.hasMoved).isFalse()
    }

    @Test
    fun `king with explicit hasMoved true`() {
        // given / when
        val king = King(Color.BLACK, Square(File.E, 8), hasMoved = true)
        // then
        assertThat(king.hasMoved).isTrue()
    }
}