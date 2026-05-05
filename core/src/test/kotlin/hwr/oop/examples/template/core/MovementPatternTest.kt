package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MovementPatternTest {

    @Test
    fun `movement pattern stores deltas and max range`() {
        // given
        val fileDelta = 1
        val rankDelta = -1
        val maxRange = 7

        // when
        val pattern = MovementPattern(fileDelta, rankDelta, maxRange)

        // then
        assertThat(pattern.fileDelta).isEqualTo(1)
        assertThat(pattern.rankDelta).isEqualTo(-1)
        assertThat(pattern.maxRange).isEqualTo(7)
    }

    @Test
    fun `movement pattern defaults maxRange to 7`() {
        // given / when
        val pattern = MovementPattern(1, 0)

        // then
        assertThat(pattern.maxRange).isEqualTo(7)
    }

    @Test
    fun `movement pattern defaults canJump to false`() {
        // given / when
        val pattern = MovementPattern(1, 0)

        // then
        assertThat(pattern.canJump).isFalse()
    }

    @Test
    fun `movement pattern defaults captureOnly and moveOnly to false`() {
        // given / when
        val pattern = MovementPattern(1, 0)

        // then
        assertThat(pattern.captureOnly).isFalse()
        assertThat(pattern.moveOnly).isFalse()
    }
}