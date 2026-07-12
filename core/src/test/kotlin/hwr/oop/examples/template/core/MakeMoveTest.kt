package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class MakeMoveTest {

	@Test
	fun `makeMove applies the move to the board`() {
		// given
		val game = Game(GameID("game-1"))
		val move = Move(Square(File.E, 2), Square(File.E, 4))
		
		// when
		val next = game.makeMove(move)
		
		// then
		assertThat(next.board.pieceAt(Square(File.E, 2))).isNull()
		assertThat(next.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
	}

	@Test
	fun `makeMove leaves the original board unchanged`() {
		// given
		val game = Game(GameID("game-1"))

		// when
		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))

		// then
		assertThat(game.board.pieceAt(Square(File.E, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 2)))
		assertThat(game.board.pieceAt(Square(File.E, 4))).isNull()
		assertThat(next.board.pieceAt(Square(File.E, 2))).isNull()
	}

	@Test
	fun `makeMove switches player from white to black without bumping turn number`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
		
		// then
		assertThat(next.turn.color).isEqualTo(Color.BLACK)
		assertThat(next.turn.number).isEqualTo(1)
	}

	@Test
	fun `makeMove bumps turn number when black completes the turn`() {
		// given
		val game = Game(GameID("game-1"), turn = Turn(1, Color.BLACK))
		
		// when
		val next = game.makeMove(Move(Square(File.E, 7), Square(File.E, 5)))
		
		// then
		assertThat(next.turn.color).isEqualTo(Color.WHITE)
		assertThat(next.turn.number).isEqualTo(2)
	}

	@Test
	fun `makeMove preserves id and status`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
		
		// then
		assertThat(next.id).isSameAs(game.id)
		assertThat(next.id.value).isEqualTo("game-1")
		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
	}

	@Test
	fun `makeMove fails when move is not in availableMoves`() {
		// given
		val game = Game(GameID("game-1"))
		val invalidMove = Move(Square(File.E, 2), Square(File.E, 5))
		
		// when / then
		assertThatThrownBy { game.makeMove(invalidMove) }
			.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")
	}

	@Test
	fun `makeMove fails when game is finished`() {
		// given
		val game = Game(
			id = GameID("game-1"),
			status = GameStatus.FINISHED,
			result = GameResult(GameEndReason.RESIGNED, Color.BLACK)
		)
		val anyMove = Move(Square(File.E, 2), Square(File.E, 4))
		
		// when / then
		assertThatThrownBy { game.makeMove(anyMove) }
			.isInstanceOf(IllegalStateException::class.java)
			.hasMessage("Game is not in progress")
	}

	@Test
	fun `makeMove leaves the board unchanged when the move is rejected`() {
		// given
		val game = Game(GameID("game-1"))
		val invalidMove = Move(Square(File.E, 2), Square(File.E, 5))
		
		// when / then
		assertThatThrownBy { game.makeMove(invalidMove) }
			.isInstanceOf(IllegalArgumentException::class.java)
		assertThat(game.board.pieceAt(Square(File.E, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 2)))
		assertThat(game.board.pieceAt(Square(File.E, 5))).isNull()
		assertThat(game.turn.color).isEqualTo(Color.WHITE)
	}

	@Test
	fun `makeMove can capture an opponent piece`() {
		// given
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(Pawn(Color.BLACK, Square(File.A, 5)))
		val game = Game(GameID("game-1"), board)
		
		// when
		val next = game.makeMove(Move(Square(File.A, 1), Square(File.A, 5)))
		
		// then
		assertThat(next.board.pieces(Color.BLACK)).isEmpty()
		assertThat(next.board.pieceAt(Square(File.A, 5)))
			.isEqualTo(Rook(Color.WHITE, Square(File.A, 5), hasMoved = true))
		assertThat(next.turn.color).isEqualTo(Color.BLACK)
	}

	@Test
	fun `makeMove sets positionStatus to CHECK when the next player's king is attacked`() {
		// given - white rook on A1, black king on H8; rook to A8 puts black in check
		val board = Board()
		board.place(Rook(Color.WHITE, Square(File.A, 1)))
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.H, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when
		val next = game.makeMove(Move(Square(File.A, 1), Square(File.A, 8)))
		
		// then
		assertThat(next.positionStatus).isEqualTo(PositionStatus.CHECK)
		assertThat(next.turn.color).isEqualTo(Color.BLACK)
	}

	@Test
	fun `makeMove sets positionStatus to NORMAL when no king is attacked`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
		
		// then
		assertThat(next.positionStatus).isEqualTo(PositionStatus.NORMAL)
	}

	@Test
	fun `makeMove rejects a move that leaves own king in check`() {
		// given - white king on E1, white rook on E2 pinned by black rook on E8
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Rook(Color.WHITE, Square(File.E, 2)))
		board.place(Rook(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when / then - moving the pinned rook off the E-file would expose the king
		assertThatThrownBy {
			game.makeMove(Move(Square(File.E, 2), Square(File.A, 2)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")
	}

	@Test
	fun `makeMove finishes the game with checkmate when the next player has no legal moves and is in check`() {
		// given - back-rank mate. Black king on H8 hemmed in by its own pawns
		// on F7 G7 H7 (no luft). White rook on D1 will deliver mate by sliding
		// to D8: rank 8 is attacked, the king has no escape squares.
		val board = Board()
		board.place(King(Color.BLACK, Square(File.H, 8)))
		board.place(Pawn(Color.BLACK, Square(File.F, 7)))
		board.place(Pawn(Color.BLACK, Square(File.G, 7)))
		board.place(Pawn(Color.BLACK, Square(File.H, 7)))
		board.place(Rook(Color.WHITE, Square(File.D, 1)))
		board.place(King(Color.WHITE, Square(File.A, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when
		val next = game.makeMove(Move(Square(File.D, 1), Square(File.D, 8)))
		
		// then
		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.positionStatus).isEqualTo(PositionStatus.CHECK)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.CHECKMATE, Color.WHITE))
		assertThat(next.availableMoves()).isEmpty()
	}

	@Test
	fun `makeMove finishes the game with stalemate when the next player has no legal moves and is not in check`() {
		// given - classic queen-and-king stalemate. Black king on H8, white king
		// on F7, white queen on G3. White plays Qg3-g6, leaving black with no
		// legal move (G7, G8, H7 are all attacked) yet not in check.
		val board = Board()
		board.place(King(Color.BLACK, Square(File.H, 8)))
		board.place(King(Color.WHITE, Square(File.F, 7)))
		board.place(Queen(Color.WHITE, Square(File.G, 3)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))
		
		// when
		val next = game.makeMove(Move(Square(File.G, 3), Square(File.G, 6)))
		
		// then
		assertThat(next.status).isEqualTo(GameStatus.FINISHED)
		assertThat(next.positionStatus).isEqualTo(PositionStatus.NORMAL)
		assertThat(next.result).isEqualTo(GameResult(GameEndReason.STALEMATE))
		assertThat(next.result?.winner).isNull()
		assertThat(next.availableMoves()).isEmpty()
	}

	@Test
	fun `makeMove keeps the game ongoing when the next player still has legal moves`() {
		// given
		val game = Game(GameID("game-1"))
		
		// when
		val next = game.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
		
		// then
		assertThat(next.status).isEqualTo(GameStatus.ONGOING)
		assertThat(next.result).isNull()
	}
}
