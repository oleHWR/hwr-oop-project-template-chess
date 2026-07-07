package hwr.oop.examples.template.core

import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameSerializationTest {
	private val json = Json {
		prettyPrint = true
		ignoreUnknownKeys = true
	}

	@Test
	fun `game can round-trip through json`() {
		// given
		val game = Game(GameID("game-1"))
			.makeMove(Move(Square(File.E, 2), Square(File.E, 4)))
			.offerDraw(Color.BLACK)

		// when
		val encoded = json.encodeToString(game)
		val decoded = json.decodeFromString<Game>(encoded)

		// then
		assertThat(decoded.id.value).isEqualTo("game-1")
		assertThat(decoded.whitePlayerId).isEqualTo("WHITE")
		assertThat(decoded.blackPlayerId).isEqualTo("BLACK")
		assertThat(decoded.turn.number).isEqualTo(1)
		assertThat(decoded.turn.color).isEqualTo(Color.BLACK)
		assertThat(decoded.status).isEqualTo(GameStatus.ONGOING)
		assertThat(decoded.positionStatus).isEqualTo(PositionStatus.NORMAL)
		assertThat(decoded.pendingDrawOfferBy).isEqualTo(Color.BLACK)
		assertThat(decoded.board.pieceAt(Square(File.E, 2))).isNull()
		assertThat(decoded.board.pieceAt(Square(File.E, 4)))
			.isEqualTo(Pawn(Color.WHITE, Square(File.E, 4), hasMoved = true))
		assertThat(decoded.board.pieces(Color.WHITE)).hasSize(16)
		assertThat(decoded.board.pieces(Color.BLACK)).hasSize(16)
	}

	@Test
	fun `finished game result can round-trip through json`() {
		// given
		val game = Game(
			id = GameID("finished-game"),
			status = GameStatus.FINISHED,
			result = GameResult(GameEndReason.RESIGNED, Color.BLACK),
		)

		// when
		val decoded = json.decodeFromString<Game>(json.encodeToString(game))

		// then
		assertThat(decoded.status).isEqualTo(GameStatus.FINISHED)
		assertThat(decoded.result).isEqualTo(GameResult(GameEndReason.RESIGNED, Color.BLACK))
		assertThat(decoded.availableMoves()).isEmpty()
	}

	@Test
	fun `core value types can round-trip through json`() {
		// given / when / then
		assertThat(json.decodeFromString<GameID>(json.encodeToString(GameID("game-1"))).value)
			.isEqualTo("game-1")
		assertThat(json.decodeFromString<Turn>(json.encodeToString(Turn(7, Color.BLACK))))
			.usingRecursiveComparison()
			.isEqualTo(Turn(7, Color.BLACK))
		assertThat(json.decodeFromString<Square>(json.encodeToString(Square(File.H, 8))))
			.isEqualTo(Square(File.H, 8))
		assertThat(
			json.decodeFromString<Move>(
				json.encodeToString(Move(Square(File.B, 1), Square(File.C, 3)))
			)
		).isEqualTo(Move(Square(File.B, 1), Square(File.C, 3)))
		assertThat(
			json.decodeFromString<GameResult>(
				json.encodeToString(GameResult(GameEndReason.CHECKMATE, Color.WHITE))
			)
		).isEqualTo(GameResult(GameEndReason.CHECKMATE, Color.WHITE))
	}

	@Test
	fun `board with every piece type can round-trip through json`() {
		// given
		val board = Board()
		board.place(Pawn(Color.WHITE, Square(File.A, 2), hasMoved = true))
		board.place(Rook(Color.BLACK, Square(File.B, 8), hasMoved = true))
		board.place(Bishop(Color.WHITE, Square(File.C, 1), hasMoved = true))
		board.place(Knight(Color.BLACK, Square(File.D, 7), hasMoved = true))
		board.place(King(Color.WHITE, Square(File.E, 1), hasMoved = true))
		board.place(Queen(Color.BLACK, Square(File.F, 8), hasMoved = true))

		// when
		val decoded = json.decodeFromString<Board>(json.encodeToString(board))

		// then
		assertThat(decoded.pieces()).containsExactlyInAnyOrder(
			Pawn(Color.WHITE, Square(File.A, 2), hasMoved = true),
			Rook(Color.BLACK, Square(File.B, 8), hasMoved = true),
			Bishop(Color.WHITE, Square(File.C, 1), hasMoved = true),
			Knight(Color.BLACK, Square(File.D, 7), hasMoved = true),
			King(Color.WHITE, Square(File.E, 1), hasMoved = true),
			Queen(Color.BLACK, Square(File.F, 8), hasMoved = true),
		)
	}

	@Test
	fun `could not load exception includes game id`() {
		// given / when
		val exception = LoadGameByIdPort.CouldNotLoadException(GameID("missing-game"))

		// then
		assertThat(exception).hasMessage("Could not load game with id: missing-game")
	}

	@Test
	fun `game with every optional field populated round-trips through json`() {
		// given — result, pendingDrawOfferBy, halfmoveClock, enPassantTarget, custom player ids
		val board = Board()
		board.place(King(Color.WHITE, Square(File.E, 1)))
		board.place(King(Color.BLACK, Square(File.E, 8)))
		val game = Game(
			id = GameID("game-1"),
			board = board,
			turn = Turn(5, Color.BLACK),
			status = GameStatus.ONGOING,
			positionStatus = PositionStatus.CHECK,
			pendingDrawOfferBy = Color.BLACK,
			whitePlayerId = "alice",
			blackPlayerId = "bob",
			halfmoveClock = 42,
			enPassantTarget = Square(File.D, 6),
		)

		// when
		val decoded = json.decodeFromString<Game>(json.encodeToString(game))

		// then
		assertThat(decoded.id.value).isEqualTo("game-1")
		assertThat(decoded.turn.number).isEqualTo(5)
		assertThat(decoded.turn.color).isEqualTo(Color.BLACK)
		assertThat(decoded.status).isEqualTo(GameStatus.ONGOING)
		assertThat(decoded.positionStatus).isEqualTo(PositionStatus.CHECK)
		assertThat(decoded.pendingDrawOfferBy).isEqualTo(Color.BLACK)
		assertThat(decoded.whitePlayerId).isEqualTo("alice")
		assertThat(decoded.blackPlayerId).isEqualTo("bob")
		assertThat(decoded.halfmoveClock).isEqualTo(42)
		assertThat(decoded.enPassantTarget).isEqualTo(Square(File.D, 6))
	}

	@Test
	fun `finished game result with a winner round-trips through json`() {
		// given
		val game = Game(
			id = GameID("game-1"),
			status = GameStatus.FINISHED,
			result = GameResult(GameEndReason.CHECKMATE, Color.WHITE),
		)

		// when
		val decoded = json.decodeFromString<Game>(json.encodeToString(game))

		// then
		assertThat(decoded.result).isEqualTo(GameResult(GameEndReason.CHECKMATE, Color.WHITE))
	}

	@Test
	fun `board pieces preserve hasMoved flag through json in both states`() {
		// given
		val board = Board()
		board.place(Pawn(Color.WHITE, Square(File.A, 2), hasMoved = false))
		board.place(Pawn(Color.BLACK, Square(File.B, 5), hasMoved = true))

		// when
		val decoded = json.decodeFromString<Board>(json.encodeToString(board))

		// then
		assertThat(decoded.pieceAt(Square(File.A, 2))).isEqualTo(
			Pawn(Color.WHITE, Square(File.A, 2), hasMoved = false)
		)
		assertThat(decoded.pieceAt(Square(File.B, 5))).isEqualTo(
			Pawn(Color.BLACK, Square(File.B, 5), hasMoved = true)
		)
	}

	@Test
	fun `every game end reason round-trips through json`() {
		for (reason in GameEndReason.entries) {
			val decoded = json.decodeFromString<GameResult>(
				json.encodeToString(GameResult(reason))
			)
			assertThat(decoded.reason).isEqualTo(reason)
		}
	}

	@Test
	fun `every position status round-trips through json`() {
		for (status in PositionStatus.entries) {
			val game = Game(GameID("game-1"), positionStatus = status)
			val decoded = json.decodeFromString<Game>(json.encodeToString(game))
			assertThat(decoded.positionStatus).isEqualTo(status)
		}
	}
}
