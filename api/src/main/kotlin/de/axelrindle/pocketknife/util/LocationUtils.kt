package de.axelrindle.pocketknife.util

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Utilities for working with [Location]s.
 */
object LocationUtils {

    private const val SEPARATOR = ";"

    /**
     * Converts a [Location] into a [String].
     *
     * @param pos The [Location] to convert.
     * @return The serialized location.
     */
    fun serializeLocation(pos: Location): String {
        val list = ArrayList<Double>()
        list.add(pos.x)
        list.add(pos.y)
        list.add(pos.z)
        list.add(pos.pitch.toDouble())
        list.add(pos.yaw.toDouble())
        return (pos.world?.name ?: "null") + SEPARATOR + list.joinToString(SEPARATOR)
    }

    /**
     * Parses a [String] into a [Location].
     *
     * @param s The serialized [Location].
     * @return The deserialized location.
     */
    fun deserializeLocation(s: String): Location {
        val list = s.split(SEPARATOR)
        val world = if (list[0] == "null") null else Bukkit.getWorld(list[0])
        return Location(world,
            list[1].toDouble(), list[2].toDouble(), list[3].toDouble(),
            list[4].toFloat(), list[5].toFloat())
    }
}