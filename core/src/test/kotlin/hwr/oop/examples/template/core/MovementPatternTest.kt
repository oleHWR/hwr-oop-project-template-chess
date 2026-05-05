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
}
