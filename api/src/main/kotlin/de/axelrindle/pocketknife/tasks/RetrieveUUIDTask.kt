package de.axelrindle.pocketknife.tasks

import com.google.gson.JsonParser
import de.axelrindle.pocketknife.util.UUIDUtils
import org.apache.commons.io.IOUtils
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Logger

/**
 * Fetches the UUID for a player name from Mojang's API.
 */
@Deprecated(
        "The custom task has been replaced by a call to Bukkit#getOfflinePlayer. " +
                "Use that instead or the async lookup method.",
        ReplaceWith("Bukkit.getOfflinePlayer(name).uniqueId", "org.bukkit.Bukkit")
)
class RetrieveUUIDTask internal constructor(
        private val nameToLookup: String
) {

    companion object {
        /** A [JsonParser] instance. */
        val parser = JsonParser()
    }

    private val logger = Logger.getLogger(javaClass.simpleName)
    private val api = "https://api.mojang.com/users/profiles/minecraft/"

    internal fun run(): UUID? {
        return when (val response = readUrl(api + nameToLookup)) {
            null -> {
                null
            }
            else -> {
                val valid =
                        if (UUIDUtils.isValid(response))
                            response
                        else
                            UUIDUtils.trimmedToFull(response)
                UUID.fromString(valid)
            }
        }
    }

    private fun readUrl(url: String): String? {
        val connection = URL(url).openConnection() as HttpURLConnection
        val stream = connection.inputStream
        val result: String? = when (connection.responseCode) {
            200 -> {
                val body = IOUtils.toString(stream, StandardCharsets.UTF_8)
                val parsed = parser.parse(body).asJsonObject.get("id")
                parsed.asString
            }
            else -> {
                logger.severe("Failed to retrieve a UUID from the URL $url")
                null
            }
        }
        stream.close()
        connection.disconnect()
        return result
    }
}