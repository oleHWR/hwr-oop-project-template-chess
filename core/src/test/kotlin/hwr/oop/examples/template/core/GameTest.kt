package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class GameTest {
	
	@Test
	fun `game stores its state`() {
		// given
		val gameID = GameID("game-1")
		val board = Board()
		val turn = Turn(4, Color.BLACK)
		val result = GameResult(GameEndReason.CHECKMATE, Color.WHITE)
		
		// when
		val game = Game(
			id = gameID,
			board = board,
			turn = turn,
			status = GameStatus.FINISHED,
			positionStatus = PositionStatus.CHECK,
			result = result
		)
		
		// then
		assertThat(game.id).isEqualTo(gameID)
		assertThat(game.board).isEqualTo(board)
		assertThat(game.turn).isEqualTo(turn)
		assertThat(game.status).isEqualTo(GameStatus.FINISHED)
		assertThat(game.positionStatus).isEqualTo(PositionStatus.CHECK)
		assertThat(game.result).isEqualTo(result)
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
		assertThat(game.positionStatus).isEqualTo(PositionStatus.NORMAL)
		assertThat(game.result).isNull()
		assertThat(game.pendingDrawOfferBy).isNull()
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
	fun `ongoing game can have a pending draw offer`() {
		// given / when
		val game = Game(GameID("game-1"), pendingDrawOfferBy = Color.WHITE)

		// then
		assertThat(game.pendingDrawOfferBy).isEqualTo(Color.WHITE)
		assertThat(game.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `game fails when finished without result`() {
		assertThatThrownBy {
			Game(GameID("game-1"), status = GameStatus.FINISHED)
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("A finished game must have a result")
	}

	@Test
	fun `game fails when ongoing with result`() {
		assertThatThrownBy {
			Game(GameID("game-1"), result = GameResult(GameEndReason.STALEMATE))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("An ongoing game cannot have a result")
	}

	@Test
	fun `finished game cannot have pending draw offer`() {
		assertThatThrownBy {
			Game(
				id = GameID("game-1"),
				status = GameStatus.FINISHED,
				result = GameResult(GameEndReason.RESIGNED, Color.BLACK),
				pendingDrawOfferBy = Color.WHITE
			)
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("A finished game cannot have a pending draw offer")
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
