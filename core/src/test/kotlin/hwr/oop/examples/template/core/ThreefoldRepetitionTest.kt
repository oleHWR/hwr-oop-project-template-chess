package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ThreefoldRepetitionTest {

	@Test
	fun `game ends in threefold repetition when the same position occurs three times`() {
		var game = Game(GameID("g"))
		repeat(2) {
			game = game.makeMove(Move(Square(File.G, 1), Square(File.F, 3)))
			game = game.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))
			game = game.makeMove(Move(Square(File.F, 3), Square(File.G, 1)))
			game = game.makeMove(Move(Square(File.F, 6), Square(File.G, 8)))
		}

		assertThat(game.status).isEqualTo(GameStatus.FINISHED)
		assertThat(game.result).isEqualTo(GameResult(GameEndReason.THREEFOLD_REPETITION))
	}

	@Test
	fun `game does not end when a position repeats only twice`() {
		var game = Game(GameID("g"))
		game = game.makeMove(Move(Square(File.G, 1), Square(File.F, 3)))
		game = game.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))
		game = game.makeMove(Move(Square(File.F, 3), Square(File.G, 1)))
		game = game.makeMove(Move(Square(File.F, 6), Square(File.G, 8)))

		assertThat(game.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `positions before the required 8-move threshold do not end the game`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))

		assertThat(game.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `many distinct positions do not trigger threefold repetition`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))
			.makeMove(Move(Square(File.G, 1), Square(File.F, 3)))
			.makeMove(Move(Square(File.B, 8), Square(File.C, 6)))
			.makeMove(Move(Square(File.F, 1), Square(File.B, 5)))
			.makeMove(Move(Square(File.A, 7), Square(File.A, 6)))
			.makeMove(Move(Square(File.B, 5), Square(File.A, 4)))
			.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))
			.makeMove(Move(Square(File.E, 1), Square(File.G, 1)))

		assertThat(game.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `returning a rook does not repeat the old castling rights`() {
		var game = Game(GameID("g"))
			.makeMove(Move(Square(File.H, 2), Square(File.H, 3)))
			.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))
			.makeMove(Move(Square(File.H, 1), Square(File.H, 2)))
			.makeMove(Move(Square(File.F, 6), Square(File.G, 8)))
			.makeMove(Move(Square(File.H, 2), Square(File.H, 1)))
			.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))

		game = game.makeMove(Move(Square(File.B, 1), Square(File.C, 3)))
		game = game.makeMove(Move(Square(File.F, 6), Square(File.G, 8)))
		game = game.makeMove(Move(Square(File.C, 3), Square(File.B, 1)))
		game = game.makeMove(Move(Square(File.G, 8), Square(File.F, 6)))

		// The pieces had occupied these squares three times, but the first
		// occurrence still had castling rights and must not count.
		assertThat(game.status).isEqualTo(GameStatus.ONGOING)

		game = game.makeMove(Move(Square(File.B, 1), Square(File.C, 3)))
		game = game.makeMove(Move(Square(File.F, 6), Square(File.G, 8)))
		game = game.makeMove(Move(Square(File.C, 3), Square(File.B, 1)))

		assertThat(game.result).isEqualTo(GameResult(GameEndReason.THREEFOLD_REPETITION))
	}
}
