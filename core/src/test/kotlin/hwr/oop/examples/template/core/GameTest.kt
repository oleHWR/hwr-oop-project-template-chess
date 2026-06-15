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
	fun `new game has standard chess starting position by default`() {
		// given / when
		val game = Game(GameID("game-1"))

		// then
		assertThat(game.board.pieces(Color.WHITE)).hasSize(16)
		assertThat(game.board.pieces(Color.BLACK)).hasSize(16)
		assertThat(game.board.pieceAt(Square(File.E, 1)))
			.isEqualTo(King(Color.WHITE, Square(File.E, 1)))
		assertThat(game.board.pieceAt(Square(File.E, 8)))
			.isEqualTo(King(Color.BLACK, Square(File.E, 8)))
		assertThat(game.board.pieceAt(Square(File.A, 2)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.A, 2)))
	}

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
			.isInstanceOf(IllegalArgumentException::class.java)
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
	fun `availableMoves excludes king moves into an attacked square`() {
		// given — black king on E8, white rook on D1 cuts the D-file
		val board = Board()
		board.place(King(Color.BLACK, Square(File.E, 8)))
		board.place(Rook(Color.WHITE, Square(File.D, 1)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.BLACK))

		// when
		val moves = game.availableMoves()

		// then — king cannot step onto D8 because the white rook attacks it
		assertThat(moves).doesNotContain(Move(Square(File.E, 8), Square(File.D, 8)))
		assertThat(moves).contains(Move(Square(File.E, 8), Square(File.F, 8)))
	}

	@Test
	fun `availableMoves excludes moves that leave own king in check (pinned piece)`() {
		// given — white king on E1, white rook on E2, black rook on E8 (pin along E-file)
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Rook(Color.WHITE, Square(File.E, 2)))
		board.place(Rook(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))

		// when
		val moves = game.availableMoves().filter { it.from == Square(File.E, 2) }

		// then — pinned rook can only move along the E-file
		assertThat(moves).allMatch { it.to.file == File.E }
		assertThat(moves).doesNotContain(Move(Square(File.E, 2), Square(File.A, 2)))
		assertThat(moves).contains(Move(Square(File.E, 2), Square(File.E, 8)))
	}

	@Test
	fun `availableMoves only allows moves that resolve check when in check`() {
		// given — white king on E1, black rook on E8 giving check, white knight on G1.
		// The knight has only one legal move: blocking on E2. All other knight moves
		// leave the king in check.
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Knight(Color.WHITE, Square(File.G, 1)))
		board.place(Rook(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))

		// when
		val knightMoves = game.availableMoves().filter { it.from == Square(File.G, 1) }

		// then — only the blocking move on E2 survives
		assertThat(knightMoves).containsExactly(Move(Square(File.G, 1), Square(File.E, 2)))
	}

	@Test
	fun `makeMove sets positionStatus to CHECK when the next player's king is attacked`() {
		// given — white rook on A1, black king on H8; rook to A8 puts black in check
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
		// given — white king on E1, white rook on E2 pinned by black rook on E8
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(Rook(Color.WHITE, Square(File.E, 2)))
		board.place(Rook(Color.BLACK, Square(File.E, 8)))
		val game = Game(GameID("game-1"), board, Turn(1, Color.WHITE))

		// when / then — moving the pinned rook off the E-file would expose the king
		assertThatThrownBy {
			game.makeMove(Move(Square(File.E, 2), Square(File.A, 2)))
		}.isInstanceOf(IllegalArgumentException::class.java)
			.hasMessage("Move is not available")
	}
}
