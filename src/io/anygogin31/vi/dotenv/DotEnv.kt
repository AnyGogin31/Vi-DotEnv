package io.anygogin31.vi.dotenv

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromStringMap

@PublishedApi
internal val fileSystem: FileSystem = SystemFileSystem

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : Any> loadEnvConfig(path: String = ".env"): T {
    val filePath: Path = Path(path)
    require(fileSystem.exists(filePath)) {
        "Env file does not exist: ${filePath.name}"
    }

    return Properties.decodeFromStringMap(
        map = parseEnvFile(filePath),
    )
}

@PublishedApi
internal fun parseEnvFile(filePath: Path): Map<String, String> {
    val properties: MutableMap<String, String> = mutableMapOf()

    fileSystem
        .source(filePath)
        .buffered()
        .use { source: Source ->
            while (true) {
                val line: String = source.readLine() ?: break
                val length: Int = line.length

                var start: Int = 0
                while (start < length && line[start] <= ' ') start++

                if (start == length || line[start] == '#') continue

                val eqIndex: Int = line.indexOf('=', start)
                if (eqIndex == -1) continue

                val key: String = line.substring(start, eqIndex).trimEnd()
                val value: String = line.substring(eqIndex + 1).trim()

                properties[key] = value
            }
        }

    return properties
}
