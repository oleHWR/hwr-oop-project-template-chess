package hwr.oop.examples.template.service

import hwr.oop.examples.template.service.api.GameActionApi
import hwr.oop.examples.template.service.api.GameApi
import hwr.oop.examples.template.service.model.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller : GameApi, GameActionApi {
	
	override fun getAvailableMoves(gameId: String?): ResponseEntity<AvailableMovesResponse> {
		TODO("Not yet implemented")
	}
	
	override fun getGame(gameId: String?): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun startGame(startGameRequest: @Valid StartGameRequest?): ResponseEntity<GameCreatedResponse> {
		TODO("Not yet implemented")
	}
	
	override fun acceptDraw(
		gameId: String?,
		drawResponseRequest: @Valid DrawResponseRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun declineDraw(
		gameId: String?,
		drawResponseRequest: @Valid DrawResponseRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun makeMove(
		gameId: String?,
		makeMoveRequest: @Valid MakeMoveRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun offerDraw(
		gameId: String?,
		drawOfferRequest: @Valid DrawOfferRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun resign(
		gameId: String?,
		resignRequest: @Valid ResignRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
}
