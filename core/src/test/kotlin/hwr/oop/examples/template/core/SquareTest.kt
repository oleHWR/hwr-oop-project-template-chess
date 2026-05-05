package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SquareTest {

    @Test
    fun `square creation with valid file and rank`() {
        // given
        val file = File.E
        val rank = 4

        // when
        val square = Square(file, rank)

        // then
        assertThat(square.file).isEqualTo(File.E)
        assertThat(square.rank).isEqualTo(4)
    }

    @Test
    fun `square creation with rank 0 throws`() {
        // given
        val file = File.A
        val rank = 0

        // when / then
        assertThatThrownBy { Square(file, rank) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `square creation with rank 9 throws`() {
        // given
        val file = File.A
        val rank = 9

        // when / then
        assertThatThrownBy { Square(file, rank) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `offset returns correct square for positive deltas`() {
        // given
        val square = Square(File.D, 4)

        // when
        val result = square.offset(2, 3)

        // then
        assertThat(result).isEqualTo(Square(File.F, 7))
    }

    @Test
    fun `offset returns correct square for negative deltas`() {
        // given
        val square = Square(File.D, 4)

        // when
        val result = square.offset(-2, -3)

        // then
        assertThat(result).isEqualTo(Square(File.B, 1))
    }

    @Test
    fun `offset returns null when file goes below A`() {
        // given
        val square = Square(File.A, 4)

        // when
        val result = square.offset(-1, 0)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `offset returns null when file goes above H`() {
        // given
        val square = Square(File.H, 4)

        // when
        val result = square.offset(1, 0)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `offset returns null when rank goes below 1`() {
        // given
        val square = Square(File.D, 1)

        // when
        val result = square.offset(0, -1)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `offset returns null when rank goes above 8`() {
        // given
        val square = Square(File.D, 8)

        // when
        val result = square.offset(0, 1)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `equal squares have same hash code`() {
        // given
        val square = Square(File.E, 4)
        val sameSquare = Square(File.E, 4)

        // when / then
        assertThat(square.hashCode()).isEqualTo(sameSquare.hashCode())
    }

    @Test
    fun `squares with same file and rank are equal`() {
        // given
        val square = Square(File.E, 4)
        val sameSquare = Square(File.E, 4)

        // when / then
        assertThat(square).isEqualTo(sameSquare)
    }

    @Test
    fun `squares with different file are not equal`() {
        // given
        val square = Square(File.A, 1)
        val otherSquare = Square(File.B, 1)

        // when / then
        assertThat(square).isNotEqualTo(otherSquare)
    }

    @Test
    fun `squares with different rank are not equal`() {
        // given
        val square = Square(File.A, 1)
        val otherSquare = Square(File.A, 2)

        // when / then
        assertThat(square).isNotEqualTo(otherSquare)
    }
}