package hwr.oop.examples.template.core

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
}

data class GameResult(
	val reason: GameEndReason,
	val winner: Color? = null,
)
