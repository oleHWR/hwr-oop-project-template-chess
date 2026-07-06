package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AvailableMovesTest {

	@Test
	fun `new game gives white twenty opening moves`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val moves = game.availableMoves()
		
		// then
		assertThat(moves).hasSize(20)
	}

	@Test
	fun `availableMoves returns moves for current player only`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Rook(Color.BLACK, Square(File.H, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when
		val moves = game.availableMoves()
		
		// then
		assertThat(moves).isNotEmpty
		assertThat(moves).hasSize(14)
		assertThat(moves).allMatch { it.from == Square(File.A, 1) }
		assertThat(moves).contains(Move(Square(File.A, 1), Square(File.A, 8)))
	}

	@Test
	fun `availableMoves returns no moves after game is finished`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		val game = Game(
			id = GameID("game-1"),
			board = board,
			status = GameStatus.FINISHED,
			result = GameResult(GameEndReason.DRAW_ACCEPTED)
		)
		
		// when
		val moves = game.availableMoves()
		
		// then
		assertThat(moves).isEmpty()
	}

	@Test
	fun `availableMoves excludes king moves into an attacked square`() {
		// given - black king on E8, white rook on D1 cuts the D-file
		val board = Board()
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.D, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.BLACK))
		
		// when
		val moves = game.availableMoves()
		
		// then - king cannot step onto D8 because the white rook attacks it
		assertThat(moves).doesNotContain(Move(Square(File.E, 8), Square(File.D, 8)))
		assertThat(moves).contains(Move(Square(File.E, 8), Square(File.F, 8)))
	}

	@Test
	fun `availableMoves excludes moves that leave own king in check (pinned piece)`() {
		// given - white king on E1, white rook on E2, black rook on E8 (pin along E-file)
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Rook(Color.WHITE, Square(File.E, 2)))
		board.place(Rook(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when
		val moves = game.availableMoves().filter { it.from == Square(File.E, 2) }
		
		// then - pinned rook can only move along the E-file
		assertThat(moves).allMatch { it.to.file == File.E }
		assertThat(moves).doesNotContain(Move(Square(File.E, 2), Square(File.A, 2)))
		assertThat(moves).contains(Move(Square(File.E, 2), Square(File.E, 8)))
	}

	@Test
	fun `availableMoves only allows moves that resolve check when in check`() {
		// given - white king on E1, black rook on E8 giving check, white knight on G1.
		// The knight has only one legal move: blocking on E2. All other knight moves
		// leave the king in check.
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Knight(Color.WHITE, Square(File.G, 1)))
		board.place(Rook(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when
		val knightMoves = game.availableMoves().filter { it.from == Square(File.G, 1) }
		
		// then - only the blocking move on E2 survives
		assertThat(knightMoves).containsExactly(Move(Square(File.G, 1), Square(File.E, 2)))
	}
}
