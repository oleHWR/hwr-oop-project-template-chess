package hwr.oop.examples.template.core

data class Square(val file: File, val rank: Int) {
	init {
		require(rank in 1..8)
	}
	
	fun offset(fileDelta: Int, rankDelta: Int): Square? {
		val newFileOrdinal = file.ordinal + fileDelta
		val newRank = rank + rankDelta
		if (newFileOrdinal !in 0..7 || newRank !in 1..8) return null
		return Square(File.entries[newFileOrdinal], newRank)
	}
}