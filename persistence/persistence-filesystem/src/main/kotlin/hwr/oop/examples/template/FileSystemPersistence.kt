package hwr.oop.examples.template

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameID
import hwr.oop.examples.template.ports.out.GameRepository
import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path

private val json = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
}

class FileSystemPersistence(
	configuration: FileSystemPersistenceConfiguration,
	private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : GameRepository {
	private val directory = configuration.directory

	override fun save(game: Game) {
		fileSystem.createDirectories(directory, mustCreate = false)
		fileSystem.write(path(game.id)) {
			writeUtf8(json.encodeToString(game))
		}
	}

	override fun loadById(gameId: GameID): Game {
		val readString = try {
			fileSystem.read(path(gameId)) {
				readUtf8()
			}
		} catch (e: FileNotFoundException) {
			throw LoadGameByIdPort.CouldNotLoadException(gameId, e)
		}
		return json.decodeFromString(readString)
	}

	private fun path(gameId: GameID): Path {
		return directory / "${gameId.value}.json"
	}
}

