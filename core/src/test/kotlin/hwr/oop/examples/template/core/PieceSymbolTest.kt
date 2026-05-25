package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PieceSymbolTest {

	@Test
	fun `white king symbol is uppercase`() {
		// given
		val king = King(Color.WHITE, Square(File.E, 1))

		// when
		val symbol = king.symbol()

		// then
		assertThat(symbol).isEqualTo("K")
	}

	@Test
	fun `black king symbol is lowercase`() {
		// given
		val king = King(Color.BLACK, Square(File.E, 8))

		// when
		val symbol = king.symbol()

		// then
		assertThat(symbol).isEqualTo("k")
	}

	@Test
	fun `black rook symbol is lowercase`() {
		// given
		val rook = Rook(Color.BLACK, Square(File.A, 8))

		// when
		val symbol = rook.symbol()

		// then
		assertThat(symbol).isEqualTo("r")
	}

	@Test
	fun `white rook symbol is uppercase`() {
		// given
		val rook = Rook(Color.WHITE, Square(File.A, 1))

		// when
		val symbol = rook.symbol()

		// then
		assertThat(symbol).isEqualTo("R")
	}

	@Test
	fun `white queen symbol is Q`() {
		// given
		val queen = Queen(Color.WHITE, Square(File.D, 1))

		// when
		val symbol = queen.symbol()

		// then
		assertThat(symbol).isEqualTo("Q")
	}

	@Test
	fun `black queen symbol is q`() {
		// given
		val queen = Queen(Color.BLACK, Square(File.D, 8))

		// when
		val symbol = queen.symbol()

		// then
		assertThat(symbol).isEqualTo("q")
	}

	@Test
	fun `white bishop symbol is B`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))

		// when
		val symbol = bishop.symbol()

		// then
		assertThat(symbol).isEqualTo("B")
	}

	@Test
	fun `black bishop symbol is b`() {
		// given
		val bishop = Bishop(Color.BLACK, Square(File.C, 8))

		// when
		val symbol = bishop.symbol()

		// then
		assertThat(symbol).isEqualTo("b")
	}

	@Test
	fun `white knight symbol is N`() {
		// given
		val knight = Knight(Color.WHITE, Square(File.B, 1))

		// when
		val symbol = knight.symbol()

		// then
		assertThat(symbol).isEqualTo("N")
	}

	@Test
	fun `black knight symbol is n`() {
		// given
		val knight = Knight(Color.BLACK, Square(File.G, 8))

		// when
		val symbol = knight.symbol()

		// then
		assertThat(symbol).isEqualTo("n")
	}

	@Test
	fun `white pawn symbol is P`() {
		// given
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))

		// when
		val symbol = pawn.symbol()

		// then
		assertThat(symbol).isEqualTo("P")
	}

	@Test
	fun `black pawn symbol is p`() {
		// given
		val pawn = Pawn(Color.BLACK, Square(File.E, 7))

		// when
		val symbol = pawn.symbol()

		// then
		assertThat(symbol).isEqualTo("p")
	}
}