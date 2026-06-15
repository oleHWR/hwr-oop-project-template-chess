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

	@Test
	fun `bishop reaches every diagonal square on an empty board`() {
		// given
		val board = Board()
		val bishop = Bishop(Color.WHITE, Square(File.A, 1))
		board.place(bishop)

		// when
		val targets = MovementFactory.availableMoves(bishop, board).map { it.to }

		// then — kills * vs / mutant: every step requires fileDelta*steps to land on the diagonal
		assertThat(targets).containsExactlyInAnyOrder(
			Square(File.B, 2),
			Square(File.C, 3),
			Square(File.D, 4),
			Square(File.E, 5),
			Square(File.F, 6),
			Square(File.G, 7),
			Square(File.H, 8),
		)
	}

	@Test
	fun `rook on H1 reaches A1 across the rank`() {
		// given
		val board = Board()
		val rook = Rook(Color.WHITE, Square(File.H, 1))
		board.place(rook)

		// when
		val targets = MovementFactory.availableMoves(rook, board).map { it.to }

		// then — kills * vs / mutant on the file axis: needs steps up to 7 with fileDelta=-1
		assertThat(targets).contains(
			Square(File.G, 1),
			Square(File.A, 1),
		)
		assertThat(targets).doesNotContain(Square(File.H, 1))
	}

	@Test
	fun `canCapture rejects same-color target directly`() {
		// given
		val whiteRook = Rook(Color.WHITE, Square(File.A, 1))
		val whitePawn = Pawn(Color.WHITE, Square(File.A, 5))

		// when
		val result = MovementFactory.canCapture(whiteRook, whitePawn)

		// then
		assertThat(result).isFalse()
	}

	@Test
	fun `canCapture accepts enemy on a reachable line directly`() {
		// given
		val whiteRook = Rook(Color.WHITE, Square(File.A, 1))
		val blackPawn = Pawn(Color.BLACK, Square(File.A, 5))

		// when
		val result = MovementFactory.canCapture(whiteRook, blackPawn)

		// then
		assertThat(result).isTrue()
	}

	@Test
	fun `squaresBetween includes both intermediate squares on a 3-step diagonal`() {
		// when
		val squares = MovementFactory.squaresBetween(Square(File.A, 1), Square(File.D, 4))

		// then — non-empty, exactly two squares: kills emptyList mutant and any off-by-one
		assertThat(squares).hasSize(2)
		assertThat(squares).containsExactly(Square(File.B, 2), Square(File.C, 3))
	}
}
