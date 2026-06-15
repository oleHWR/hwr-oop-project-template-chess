package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoardSetupTest {

	@Test
	fun `standardSetup places 32 pieces`() {
		// when
		val board = Board.standardSetup()

		// then
		assertThat(board.pieces(Color.WHITE)).hasSize(16)
		assertThat(board.pieces(Color.BLACK)).hasSize(16)
	}

	@Test
	fun `standardSetup places eight white pawns on rank 2`() {
		// given
		val board = Board.standardSetup()

		// when
		val whitePawnSquares = board.pieces(Color.WHITE)
			.filter { it.type == PieceType.PAWN }
			.map { it.position }

		// then
		assertThat(whitePawnSquares).containsExactlyInAnyOrderElementsOf(
			File.entries.map { Square(it, 2) }
		)
	}

	@Test
	fun `standardSetup places eight black pawns on rank 7`() {
		// given
		val board = Board.standardSetup()

		// when
		val blackPawnSquares = board.pieces(Color.BLACK)
			.filter { it.type == PieceType.PAWN }
			.map { it.position }

		// then
		assertThat(blackPawnSquares).containsExactlyInAnyOrderElementsOf(
			File.entries.map { Square(it, 7) }
		)
	}

	@Test
	fun `standardSetup places white back rank pieces on rank 1`() {
		// given
		val board = Board.standardSetup()

		// then
		assertThat(board.pieceAt(Square(File.A, 1))).isEqualTo(Rook(Color.WHITE, Square(File.A, 1)))
		assertThat(board.pieceAt(Square(File.B, 1))).isEqualTo(Knight(Color.WHITE, Square(File.B, 1)))
		assertThat(board.pieceAt(Square(File.C, 1))).isEqualTo(Bishop(Color.WHITE, Square(File.C, 1)))
		assertThat(board.pieceAt(Square(File.D, 1))).isEqualTo(Queen(Color.WHITE, Square(File.D, 1)))
		assertThat(board.pieceAt(Square(File.E, 1))).isEqualTo(King(Color.WHITE, Square(File.E, 1)))
		assertThat(board.pieceAt(Square(File.F, 1))).isEqualTo(Bishop(Color.WHITE, Square(File.F, 1)))
		assertThat(board.pieceAt(Square(File.G, 1))).isEqualTo(Knight(Color.WHITE, Square(File.G, 1)))
		assertThat(board.pieceAt(Square(File.H, 1))).isEqualTo(Rook(Color.WHITE, Square(File.H, 1)))
	}

	@Test
	fun `standardSetup places black back rank pieces on rank 8`() {
		// given
		val board = Board.standardSetup()

		// then
		assertThat(board.pieceAt(Square(File.A, 8))).isEqualTo(Rook(Color.BLACK, Square(File.A, 8)))
		assertThat(board.pieceAt(Square(File.B, 8))).isEqualTo(Knight(Color.BLACK, Square(File.B, 8)))
		assertThat(board.pieceAt(Square(File.C, 8))).isEqualTo(Bishop(Color.BLACK, Square(File.C, 8)))
		assertThat(board.pieceAt(Square(File.D, 8))).isEqualTo(Queen(Color.BLACK, Square(File.D, 8)))
		assertThat(board.pieceAt(Square(File.E, 8))).isEqualTo(King(Color.BLACK, Square(File.E, 8)))
		assertThat(board.pieceAt(Square(File.F, 8))).isEqualTo(Bishop(Color.BLACK, Square(File.F, 8)))
		assertThat(board.pieceAt(Square(File.G, 8))).isEqualTo(Knight(Color.BLACK, Square(File.G, 8)))
		assertThat(board.pieceAt(Square(File.H, 8))).isEqualTo(Rook(Color.BLACK, Square(File.H, 8)))
	}

	@Test
	fun `standardSetup leaves ranks 3 to 6 empty`() {
		// given
		val board = Board.standardSetup()

		// then
		for (rank in 3..6) {
			for (file in File.entries) {
				assertThat(board.pieceAt(Square(file, rank)))
					.describedAs("square $file$rank should be empty")
					.isNull()
			}
		}
	}

	@Test
	fun `standardSetup pieces are all unmoved`() {
		// given
		val board = Board.standardSetup()

		// when
		val pieces = board.pieces(Color.WHITE) + board.pieces(Color.BLACK)

		// then
		assertThat(pieces).allMatch { !it.hasMoved }
	}

	@Test
	fun `standardSetup renders as initial chess position`() {
		// given
		val board = Board.standardSetup()

		// when
		val text = board.showBoard()

		// then
		assertThat(text).isEqualTo(
			"rnbqkbnr\n" +
					"pppppppp\n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"PPPPPPPP\n" +
					"RNBQKBNR"
		)
	}
}
