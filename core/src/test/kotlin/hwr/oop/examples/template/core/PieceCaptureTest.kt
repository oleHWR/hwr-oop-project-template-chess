package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PieceCaptureTest {

	@Test
	fun `piece cannot capture target of same color`() {
		// given
		val whiteRook = Rook(Color.WHITE, Square(File.A, 1))
		val whitePawn = Pawn(Color.WHITE, Square(File.A, 5))
		// when
		val result = whiteRook.canCapture(whitePawn)
		// then
		assertThat(result).isFalse()
	}

	@Test
	fun `rook captures enemy on same file within range`() {
		// given
		val whiteRook = Rook(Color.WHITE, Square(File.A, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.A, 5))
		// when
		val result = whiteRook.canCapture(blackPawn)
		// then
		assertThat(result).isTrue()
	}

	@Test
	fun `rook captures enemy on same rank`() {
		// given
		val whiteRook = Rook(Color.WHITE, Square(File.A, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.H, 1))
		// when / then
		assertThat(whiteRook.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `rook cannot capture enemy on diagonal`() {
		// given
		val whiteRook = Rook(Color.WHITE, Square(File.A, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.B, 2))
		// when / then
		assertThat(whiteRook.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `bishop captures enemy on diagonal`() {
		// given
		val whiteBishop = Bishop(Color.WHITE, Square(File.C, 1))
		val blackKnight = Knight(Color.BLACK, Square(File.H, 6))
		// when / then
		assertThat(whiteBishop.canCapture(blackKnight)).isTrue()
	}

	@Test
	fun `bishop cannot capture enemy on same rank`() {
		// given
		val whiteBishop = Bishop(Color.WHITE, Square(File.C, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.H, 1))
		// when / then
		assertThat(whiteBishop.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `knight captures enemy on L-shape`() {
		// given
		val whiteKnight = Knight(Color.WHITE, Square(File.B, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.C, 3))
		// when / then
		assertThat(whiteKnight.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `knight captures enemy on wide L-shape`() {
		// given
		val whiteKnight = Knight(Color.WHITE, Square(File.B, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.D, 2))
		// when / then
		assertThat(whiteKnight.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `knight cannot capture enemy on adjacent square`() {
		// given
		val whiteKnight = Knight(Color.WHITE, Square(File.B, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.B, 2))
		// when / then
		assertThat(whiteKnight.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `queen captures enemy on file`() {
		// given
		val whiteQueen = Queen(Color.WHITE, Square(File.D, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.D, 8))
		// when / then
		assertThat(whiteQueen.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `queen captures enemy on diagonal`() {
		// given
		val whiteQueen = Queen(Color.WHITE, Square(File.D, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.H, 5))
		// when / then
		assertThat(whiteQueen.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `queen cannot capture enemy off any line`() {
		// given
		val whiteQueen = Queen(Color.WHITE, Square(File.D, 1))
		val blackKnight = Knight(Color.BLACK, Square(File.E, 3))
		// when / then
		assertThat(whiteQueen.canCapture(blackKnight)).isFalse()
	}

	@Test
	fun `king captures adjacent enemy`() {
		// given
		val whiteKing = King(Color.WHITE, Square(File.E, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.E, 2))
		// when / then
		assertThat(whiteKing.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `king cannot capture enemy two squares away`() {
		// given
		val whiteKing = King(Color.WHITE, Square(File.E, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.E, 3))
		// when / then
		assertThat(whiteKing.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `white pawn captures enemy on left forward diagonal`() {
		// given
		val whitePawn = Pawn(Color.WHITE, Square(File.E, 4))
		val blackPawn = Pawn(Color.BLACK, Square(File.D, 5))
		// when / then
		assertThat(whitePawn.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `white pawn captures enemy on right forward diagonal`() {
		// given
		val whitePawn = Pawn(Color.WHITE, Square(File.E, 4))
		val blackPawn = Pawn(Color.BLACK, Square(File.F, 5))
		// when / then
		assertThat(whitePawn.canCapture(blackPawn)).isTrue()
	}

	@Test
	fun `white pawn cannot capture enemy directly in front`() {
		// given
		val whitePawn = Pawn(Color.WHITE, Square(File.E, 4))
		val blackPawn = Pawn(Color.BLACK, Square(File.E, 5))
		// when / then
		assertThat(whitePawn.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `pawn cannot capture two squares ahead even on initial double step`() {
		// given
		val whitePawn = Pawn(Color.WHITE, Square(File.E, 2))
		val blackPawn = Pawn(Color.BLACK, Square(File.E, 4))
		// when / then
		assertThat(whitePawn.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `white pawn cannot capture backward diagonal`() {
		// given
		val whitePawn = Pawn(Color.WHITE, Square(File.E, 4))
		val blackPawn = Pawn(Color.BLACK, Square(File.D, 3))
		// when / then
		assertThat(whitePawn.canCapture(blackPawn)).isFalse()
	}

	@Test
	fun `black pawn captures enemy on its forward diagonal`() {
		// given
		val blackPawn = Pawn(Color.BLACK, Square(File.E, 5))
		val whitePawn = Pawn(Color.WHITE, Square(File.D, 4))
		// when / then
		assertThat(blackPawn.canCapture(whitePawn)).isTrue()
	}

	@Test
	fun `pawn cannot capture distant diagonal`() {
		// given
		val whitePawn = Pawn(Color.WHITE, Square(File.E, 4))
		val blackKnight = Knight(Color.BLACK, Square(File.C, 6))
		// when / then
		assertThat(whitePawn.canCapture(blackKnight)).isFalse()
	}

	@Test
	fun `canCapture works through Piece interface reference`() {
		// given
		val whiteRook: Piece = Rook(Color.WHITE, Square(File.A, 1))
		val blackPawn: Piece = Pawn(Color.BLACK, Square(File.A, 5))
		val whitePawn: Piece = Pawn(Color.WHITE, Square(File.A, 5))

		// when
		val canCaptureEnemy = whiteRook.canCapture(blackPawn)
		val canCaptureFriend = whiteRook.canCapture(whitePawn)

		// then
		assertThat(canCaptureEnemy).isTrue()
		assertThat(canCaptureFriend).isFalse()
	}
}
