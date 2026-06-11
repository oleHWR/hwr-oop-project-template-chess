package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MovementFactoryTest {

	@Test
	fun `squaresBetween returns squares on same file`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.A, 1), Square(File.A, 4))

		// then
		assertThat(squares).containsExactly(Square(File.A, 2), Square(File.A, 3))
	}

	@Test
	fun `squaresBetween returns squares downward on same file`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.A, 4), Square(File.A, 1))

		// then
		assertThat(squares).containsExactly(Square(File.A, 3), Square(File.A, 2))
	}

	@Test
	fun `squaresBetween returns squares on same rank`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.D, 4), Square(File.A, 4))

		// then
		assertThat(squares).containsExactly(Square(File.C, 4), Square(File.B, 4))
	}

	@Test
	fun `squaresBetween returns squares on diagonal`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.A, 1), Square(File.D, 4))

		// then
		assertThat(squares).containsExactly(Square(File.B, 2), Square(File.C, 3))
	}

	@Test
	fun `squaresBetween returns no squares for adjacent squares`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.E, 1), Square(File.E, 2))

		// then
		assertThat(squares).isEmpty()
	}

	@Test
	fun `squaresBetween returns no squares for unaligned squares`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.A, 1), Square(File.B, 3))

		// then
		assertThat(squares).isEmpty()
	}

	@Test
	fun `rook can move until its path is blocked`() {
		// given
		val board = Board()
		val rook = Rook(Color.WHITE, Square(File.A, 1))
		board.place(rook)
		board.place(Pawn(Color.WHITE, Square(File.A, 3)))

		// when
		val moves = MovementFactory.availableMoves(rook, board)

		// then
		assertThat(moves).contains(Move(Square(File.A, 1), Square(File.A, 2)))
		assertThat(moves).doesNotContain(Move(Square(File.A, 1), Square(File.A, 3)))
		assertThat(moves).doesNotContain(Move(Square(File.A, 1), Square(File.A, 4)))
	}

	@Test
	fun `rook can capture opponent but cannot move beyond it`() {
		// given
		val board = Board()
		val rook = Rook(Color.WHITE, Square(File.A, 1))
		board.place(rook)
		board.place(Pawn(Color.BLACK, Square(File.A, 3)))

		// when
		val moves = MovementFactory.availableMoves(rook, board)

		// then
		assertThat(moves).contains(Move(Square(File.A, 1), Square(File.A, 3)))
		assertThat(moves).doesNotContain(Move(Square(File.A, 1), Square(File.A, 4)))
	}

	@Test
	fun `knight can jump over occupied squares`() {
		// given
		val board = Board()
		val knight = Knight(Color.WHITE, Square(File.B, 1))
		board.place(knight)
		board.place(Pawn(Color.WHITE, Square(File.B, 2)))

		// when
		val moves = MovementFactory.availableMoves(knight, board)

		// then
		assertThat(moves).contains(
			Move(Square(File.B, 1), Square(File.C, 3)),
			Move(Square(File.B, 1), Square(File.D, 2))
		)
	}

	@Test
	fun `pawn moves forward and captures diagonally`() {
		// given
		val board = Board()
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		board.place(pawn)
		board.place(Pawn(Color.BLACK, Square(File.D, 3)))

		// when
		val moves = MovementFactory.availableMoves(pawn, board)

		// then
		assertThat(moves).contains(
			Move(Square(File.E, 2), Square(File.E, 3)),
			Move(Square(File.E, 2), Square(File.E, 4)),
			Move(Square(File.E, 2), Square(File.D, 3))
		)
		assertThat(moves).doesNotContain(Move(Square(File.E, 2), Square(File.F, 3)))
	}

	@Test
	fun `pawn cannot move through occupied square`() {
		// given
		val board = Board()
		val pawn = Pawn(Color.WHITE, Square(File.E, 2))
		board.place(pawn)
		board.place(Pawn(Color.BLACK, Square(File.E, 3)))

		// when
		val moves = MovementFactory.availableMoves(pawn, board)

		// then
		assertThat(moves).doesNotContain(
			Move(Square(File.E, 2), Square(File.E, 3)),
			Move(Square(File.E, 2), Square(File.E, 4))
		)
	}
}
