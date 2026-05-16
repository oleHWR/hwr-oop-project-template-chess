package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameTest {
	
	@Test
	fun `game stores id board turn and status`() {
		// given
		val gameID = GameID("game-1")
		val board = Board()
		val turn = Turn(4, Color.BLACK)
		
		// when
		val game = Game(gameID, board, turn, GameStatus.CHECK)
		
		// then
		assertThat(game.id).isEqualTo(gameID)
		assertThat(game.board).isEqualTo(board)
		assertThat(game.turn).isEqualTo(turn)
		assertThat(game.status).isEqualTo(GameStatus.CHECK)
	}
	
	@Test
	fun `new game has white turn and ongoing status by default`() {
		// given
		val gameID = GameID("game-1")
		
		// when
		val game = Game(gameID)
		
		// then
		assertThat(game.turn.number).isEqualTo(1)
		assertThat(game.turn.color).isEqualTo(Color.WHITE)
		assertThat(game.status).isEqualTo(GameStatus.ONGOING)
	}
	
	@Test
	fun `showBoard shows turn and board`() {
		// given
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		val game = Game(GameID("game-1"), board, Turn(2, Color.BLACK))
		
		// when
		val text = game.showBoard()
		
		// then
		assertThat(text).isEqualTo(
			"Turn 2:\n\n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"        \n" +
					"    K   "
		)
	}
}
