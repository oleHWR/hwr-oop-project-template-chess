package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MoveHistoryTest {

	@Test
	fun `new game has empty move history`() {
		val game = Game(GameID("g"))

		assertThat(game.moveHistory).isEmpty()
	}

	@Test
	fun `moveHistory records every move in order`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))
			.makeMove(Move(Square(File.G, 1), Square(File.F, 3)))

		assertThat(game.moveHistory).containsExactly(
			Move(Square(File.E, 2), Square(File.E, 4)),
			Move(Square(File.E, 7), Square(File.E, 5)),
			Move(Square(File.G, 1), Square(File.F, 3)),
		)
	}

	@Test
	fun `undo trims the move history to what preceded the last move`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))

		val reverted = game.undo()

		assertThat(reverted.moveHistory).containsExactly(
			Move(Square(File.E, 2), Square(File.E, 4))
		)
	}

	@Test
	fun `move history survives draw offer, accept, decline and resign`() {
		val game = Game(GameID("g"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		val offered = game.offerDraw(Color.BLACK)
		val declined = offered.declineDraw()
		val accepted = offered.acceptDraw()
		val resigned = game.resign(Color.BLACK)

		val expected = listOf(Move(Square(File.E, 2), Square(File.E, 4)))
		assertThat(offered.moveHistory).isEqualTo(expected)
		assertThat(declined.moveHistory).isEqualTo(expected)
		assertThat(accepted.moveHistory).isEqualTo(expected)
		assertThat(resigned.moveHistory).isEqualTo(expected)
	}
}
