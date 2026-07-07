package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

enum class GameStatus {
	ONGOING,
	FINISHED,
}

enum class PositionStatus {
	NORMAL,
	CHECK,
}

enum class GameEndReason {
	CHECKMATE,
	STALEMATE,
	DRAW_ACCEPTED,
	RESIGNED,
	FIFTY_MOVE_RULE,
}

@Serializable
data class GameResult(
	val reason: GameEndReason,
	val winner: Color? = null,
)
