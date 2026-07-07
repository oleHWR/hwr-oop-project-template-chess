package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
data class Move(
	val from: Square,
	val to: Square,
	val promotion: PieceType? = null,
)
