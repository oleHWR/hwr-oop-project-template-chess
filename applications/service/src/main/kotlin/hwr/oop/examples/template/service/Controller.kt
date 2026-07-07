package hwr.oop.examples.template.service

import hwr.oop.examples.template.core.Color
import hwr.oop.examples.template.core.File
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameEndReason
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.core.GameStatus
import hwr.oop.examples.template.core.Move
import hwr.oop.examples.template.core.PieceType
import hwr.oop.examples.template.core.Square
import hwr.oop.examples.template.ports.out.GameRepository
import hwr.oop.examples.template.service.api.GameActionApi
import hwr.oop.examples.template.service.api.GameApi
import hwr.oop.examples.template.service.model.AvailableMove
import hwr.oop.examples.template.service.model.AvailableMovesResponse
import hwr.oop.examples.template.service.model.BoardPiece
import hwr.oop.examples.template.service.model.DrawOfferRequest
import hwr.oop.examples.template.service.model.DrawResponseRequest
import hwr.oop.examples.template.service.model.GameCreatedResponse
import hwr.oop.examples.template.service.model.GameState
import hwr.oop.examples.template.service.model.MakeMoveRequest
import hwr.oop.examples.template.service.model.Piece
import hwr.oop.examples.template.service.model.ResignRequest
import hwr.oop.examples.template.service.model.StartGameRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import hwr.oop.examples.template.service.model.Square as SquareDto

@RestController
class Controller(
	private val persistence: GameRepository,
) : GameApi, GameActionApi {

	override fun getAvailableMoves(gameId: String?): ResponseEntity<AvailableMovesResponse> {
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		val response = AvailableMovesResponse(
			game.availableMoves().map { move ->
				AvailableMove(move.from.toDto(), move.to.toDto()).apply {
					promotionPiece(move.promotion?.name)
				}
			}
		)
		return ResponseEntity.ok(response)
	}

	override fun getGame(gameId: String?): ResponseEntity<GameState> {
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		return ResponseEntity.ok(game.toState())
	}

	override fun startGame(startGameRequest: @Valid StartGameRequest?): ResponseEntity<GameCreatedResponse> {
		val request = requireNotNull(startGameRequest)
		val gameId = UUID.randomUUID().toString()
		val game = Game(
			id = GameID(gameId),
			whitePlayerId = request.whitePlayerId,
			blackPlayerId = request.blackPlayerId,
		)
		persistence.save(game)
		return ResponseEntity.ok(GameCreatedResponse(gameId))
	}

	override fun acceptDraw(
		gameId: String?,
		drawResponseRequest: @Valid DrawResponseRequest?,
	): ResponseEntity<GameState> {
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		val updated = game.acceptDraw()
		persistence.save(updated)
		return ResponseEntity.ok(updated.toState())
	}

	override fun declineDraw(
		gameId: String?,
		drawResponseRequest: @Valid DrawResponseRequest?,
	): ResponseEntity<GameState> {
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		val updated = game.declineDraw()
		persistence.save(updated)
		return ResponseEntity.ok(updated.toState())
	}

	override fun makeMove(
		gameId: String?,
		makeMoveRequest: @Valid MakeMoveRequest?,
	): ResponseEntity<GameState> {
		val request = requireNotNull(makeMoveRequest)
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		val move = Move(
			from = request.from.toDomain(),
			to = request.to.toDomain(),
			promotion = request.promotionPiece?.let { PieceType.valueOf(it) },
		)
		val updated = game.makeMove(move)
		persistence.save(updated)
		return ResponseEntity.ok(updated.toState())
	}

	override fun offerDraw(
		gameId: String?,
		drawOfferRequest: @Valid DrawOfferRequest?,
	): ResponseEntity<GameState> {
		val request = requireNotNull(drawOfferRequest)
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		val updated = game.offerDraw(game.colorFor(request.playerId))
		persistence.save(updated)
		return ResponseEntity.ok(updated.toState())
	}

	override fun resign(
		gameId: String?,
		resignRequest: @Valid ResignRequest?,
	): ResponseEntity<GameState> {
		val request = requireNotNull(resignRequest)
		val game = persistence.loadById(GameID(requireNotNull(gameId)))
		val updated = game.resign(game.colorFor(request.playerId))
		persistence.save(updated)
		return ResponseEntity.ok(updated.toState())
	}
}

private fun Square.toDto(): SquareDto = SquareDto(file.name, rank)

private fun SquareDto.toDomain(): Square = Square(File.valueOf(file), rank)

private fun Game.colorFor(playerId: String): Color = when (playerId) {
	whitePlayerId -> Color.WHITE
	blackPlayerId -> Color.BLACK
	else -> throw IllegalArgumentException("Unknown player ID: $playerId")
}

private fun Game.toState(): GameState {
	val boardPieces = board.pieces().map { piece ->
		BoardPiece(piece.position.toDto(), Piece(piece.color.name, piece.type.name))
	}
	val state = GameState(
		id.value,
		statusFor(status, result?.reason),
		turn.color.name,
		whitePlayerId,
		blackPlayerId,
		boardPieces,
	)
	state.pendingDrawOfferBy(pendingDrawOfferBy?.let { playerIdFor(it) })
	return state
}

private fun Game.playerIdFor(color: Color): String = when (color) {
	Color.WHITE -> whitePlayerId
	Color.BLACK -> blackPlayerId
}

private fun statusFor(status: GameStatus, reason: GameEndReason?): String {
	if (status == GameStatus.ONGOING) return "IN_PROGRESS"
	return when (reason) {
		GameEndReason.CHECKMATE -> "CHECKMATE"
		GameEndReason.STALEMATE -> "STALEMATE"
		GameEndReason.RESIGNED -> "RESIGNED"
		else -> "DRAW"
	}
}
