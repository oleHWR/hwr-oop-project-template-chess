package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class BoardTest {
	
	@Test
	fun `squareAt returns square for file and rank`() {
		// given
		val board = Board()
		
		// when
		val square = board.squareAt(File.H, 8)
		
		// then
		assertThat(square).isEqualTo(Square(File.H, 8))
	}

	@Test
	fun `squareAt fails when rank is below board`() {
		// given
		val board = Board()

		// when / then
		assertThatThrownBy {
			board.squareAt(File.A, 0)
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Rank must be between 1 and 8")
	}

	@Test
	fun `squareAt fails when rank is above board`() {
		// given
		val board = Board()

		// when / then
		assertThatThrownBy {
			board.squareAt(File.A, 9)
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Rank must be between 1 and 8")
	}
	
	@Test
	fun `pieceAt returns placed piece`() {
		// given
		val board = Board()
		val rook = Rook(Color.WHITE, Square(File.A, 1))
		
		// when
		board.place(rook)
		
		// then
		assertThat(board.pieceAt(Square(File.A, 1))).isEqualTo(rook)
	}
	
	@Test
	fun `place fails when square is already occupied`() {
		// given
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		
		// when / then
		assertThatThrownBy {
			board.place(Rook(Color.WHITE, Square(File.E, 1)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Square is already occupied")
	}
	
	@Test
	fun `pieceAt returns null when square has no piece`() {
		// given
		val board = Board()
		val square = Square(File.A, 1)
		
		// when
		val piece = board.pieceAt(square)
		
		// then
		assertThat(piece).isNull()
	}
	
	@Test
	fun `pieceAt returns piece on square`() {
		// given
		val board = Board()
		val king = King(Color.BLACK, Square(File.E, 8))
		
		// when
		board.place(king)
		val foundPiece = board.pieceAt(Square(File.E, 8))
		
		// then
		assertThat(foundPiece).isEqualTo(king)
	}
	
	@Test
	fun `applyMove moves piece to target square`() {
		// given
		val board = Board()
		val king = King(Color.WHITE, Square(File.E, 1))
		board.place(king)
		
		// when
		board.applyMove(Move(Square(File.E, 1), Square(File.E, 2)))
		
		// then
		val movedKing = board.pieceAt(Square(File.E, 2))
		assertThat(board.pieceAt(Square(File.E, 1))).isNull()
		assertThat(movedKing).isInstanceOf(King::class.java)
		assertThat(movedKing?.color).isEqualTo(Color.WHITE)
		assertThat(movedKing?.hasMoved).isTrue()
	}
	
	@Test
	fun `applyMove fails when no piece is on start square`() {
		// given
		val board = Board()
		
		// when / then
		assertThatThrownBy {
			board.applyMove(Move(Square(File.E, 1), Square(File.E, 2)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("No piece on start square")
	}
	
	@Test
	fun `applyMove fails when target square is occupied`() {
		// given
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Rook(Color.WHITE, Square(File.E, 2)))
		
		// when / then
		assertThatThrownBy {
			board.applyMove(Move(Square(File.E, 1), Square(File.E, 2)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Target square is already occupied")
	}
	
	@Test
	fun `showBoard shows empty board`() {
		// given
		val board = Board()
		
		// when
		val text = board.showBoard()
		
		// then
		assertThat(text).isEqualTo(
			"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        "
		)
	}
	
	@Test
	fun `showBoard shows pieces on the board`() {
		// given
		val board = Board()
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Bishop(Color.WHITE, Square(File.E, 2)))
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		
		// when
		val text = board.showBoard()
		
		// then
		assertThat(text).isEqualTo(
			"    k   \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"    B   \n" +
					"R       "
		)
	}

	@Test
	fun `validCapture returns false when attacker square is empty`() {
		// given
		val board = Board()
		board.place(Pawn(Color.BLACK, Square(File.A, 4)))

		// when
		val result = board.validCapture(Square(File.A, 1), Square(File.A, 4))

		// then
		assertThat(result).isFalse()
	}

	@Test
	fun `validCapture returns false when target square is empty`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))

		// when
		val result = board.validCapture(Square(File.A, 1), Square(File.A, 4))

		// then
		assertThat(result).isFalse()
	}

	@Test
	fun `validCapture returns false when piece cannot capture target by movement`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.BLACK, Square(File.B, 2)))

		// when
		val result = board.validCapture(Square(File.A, 1), Square(File.B, 2))

		// then
		assertThat(result).isFalse()
	}

	@Test
	fun `validCapture returns true for knight even when another piece is between`() {
		// given
		val board = Board()
		board.place(Knight(Color.WHITE, Square(File.B, 1)))
		board.place(Pawn(Color.WHITE, Square(File.B, 2)))
		board.place(Pawn(Color.BLACK, Square(File.C, 3)))

		// when
		val result = board.validCapture(Square(File.B, 1), Square(File.C, 3))

		// then
		assertThat(result).isTrue()
	}

	@Test
	fun `validCapture returns true when path to target is clear`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.BLACK, Square(File.A, 4)))

		// when
		val result = board.validCapture(Square(File.A, 1), Square(File.A, 4))

		// then
		assertThat(result).isTrue()
	}

	@Test
	fun `validCapture returns false when path to target is blocked`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.WHITE, Square(File.A, 2)))
		board.place(Pawn(Color.BLACK, Square(File.A, 4)))

		// when
		val result = board.validCapture(Square(File.A, 1), Square(File.A, 4))

		// then
		assertThat(result).isFalse()
	}

	@Test
	fun `squaresBetween returns squares on same file`() {
		// given
		val board = Board()

		// when
		val squares = board.squaresBetween(Square(File.A, 1), Square(File.A, 4))

		// then
		assertThat(squares).containsExactly(Square(File.A, 2), Square(File.A, 3))
	}

	@Test
	fun `squaresBetween returns squares downward on same file`() {
		// given
		val board = Board()

		// when
		val squares = board.squaresBetween(Square(File.A, 4), Square(File.A, 1))

		// then
		assertThat(squares).containsExactly(Square(File.A, 3), Square(File.A, 2))
	}

	@Test
	fun `squaresBetween returns squares on same rank`() {
		// given
		val board = Board()

		// when
		val squares = board.squaresBetween(Square(File.D, 4), Square(File.A, 4))

		// then
		assertThat(squares).containsExactly(Square(File.C, 4), Square(File.B, 4))
	}

	@Test
	fun `squaresBetween returns squares on diagonal`() {
		// given
		val board = Board()

		// when
		val squares = board.squaresBetween(Square(File.A, 1), Square(File.D, 4))

		// then
		assertThat(squares).containsExactly(Square(File.B, 2), Square(File.C, 3))
	}

	@Test
	fun `squaresBetween returns empty list for adjacent squares`() {
		// given
		val board = Board()

		// when
		val squares = board.squaresBetween(Square(File.E, 1), Square(File.E, 2))

		// then
		assertThat(squares).isEmpty()
	}

	@Test
	fun `squaresBetween stops at edge when target is not on step path`() {
		// given
		val board = Board()

		// when
		val squares = board.squaresBetween(Square(File.A, 1), Square(File.B, 3))

		// then
		assertThat(squares).containsExactly(
			Square(File.B, 2),
			Square(File.C, 3),
			Square(File.D, 4),
			Square(File.E, 5),
			Square(File.F, 6),
			Square(File.G, 7),
			Square(File.H, 8)
		)
	}
}
