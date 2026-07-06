package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ResignTest {

	@Test
	fun `resign finishes the game with RESIGNED and the opponent as winner`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val resigned = game.resign(Color.WHITE)
		
		// then
		assertThat(resigned.status).isEqualTo(GameStatus.FINISHED)
		assertThat(resigned.result).isEqualTo(GameResult(GameEndReason.RESIGNED, Color.BLACK))
	}

	@Test
	fun `black can resign too leaving white as winner`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val resigned = game.resign(Color.BLACK)
		
		// then
		assertThat(resigned.result).isEqualTo(GameResult(GameEndReason.RESIGNED, Color.WHITE))
	}

	@Test
	fun `resign can be called even when it is the opponent's turn`() {
		// given - black to move
		val game = Game(GameID("game-1"), turn = Turn(1, Color.BLACK))
		
		// when - white resigns out of turn
		val resigned = game.resign(Color.WHITE)
		
		// then
		assertThat(resigned.status).isEqualTo(GameStatus.FINISHED)
		assertThat(resigned.result).isEqualTo(GameResult(GameEndReason.RESIGNED, Color.BLACK))
	}

	@Test
	fun `resign clears any pending draw offer`() {
		// given
		val game = Game(GameID("game-1")).offerDraw(Color.WHITE)
		
		// when
		val resigned = game.resign(Color.WHITE)
		
		// then
		assertThat(resigned.pendingDrawOfferBy).isNull()
		assertThat(resigned.status).isEqualTo(GameStatus.FINISHED)
	}

	@Test
	fun `resign fails when the game is already finished`() {
		// given
		val game = Game(
			id = GameID("game-1"),
			status = GameStatus.FINISHED,
			result = GameResult(GameEndReason.STALEMATE)
		)
		
		// when / then
		assertThatThrownBy { game.resign(Color.WHITE) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Game is not in progress")
	}

	@Test
	fun `resign leaves the board untouched`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val resigned = game.resign(Color.BLACK)
		
		// then - pieces still in starting position
		assertThat(resigned.board).isSameAs(game.board)
		assertThat(resigned.board.pieceAt(Square(File.E, 1)))
			.isEqualTo(King(Color.WHITE, Square(File.E, 1)))
	}
}
