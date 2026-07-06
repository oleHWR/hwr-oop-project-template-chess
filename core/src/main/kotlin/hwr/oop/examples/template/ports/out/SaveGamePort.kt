package hwr.oop.examples.template.ports.out

import hwr.oop.examples.template.core.Game

interface SaveGamePort {
	fun save(game: Game)
}
