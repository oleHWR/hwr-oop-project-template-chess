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
	fun `black rook symbol is lowercase`() {
		// given
		val rook = Rook(Color.BLACK, Square(File.A, 8))
		
		// when
		val symbol = rook.symbol()
		
		// then
		assertThat(symbol).isEqualTo("r")
	}
	
	@Test
	fun `queen symbol is Q`() {
		// given
		val queen = Queen(Color.WHITE, Square(File.D, 1))
		
		// when
		val symbol = queen.symbol()
		
		// then
		assertThat(symbol).isEqualTo("Q")
	}
	
	@Test
	fun `bishop symbol is B`() {
		// given
		val bishop = Bishop(Color.WHITE, Square(File.C, 1))
		
		// when
		val symbol = bishop.symbol()
		
		// then
		assertThat(symbol).isEqualTo("B")
	}
}
