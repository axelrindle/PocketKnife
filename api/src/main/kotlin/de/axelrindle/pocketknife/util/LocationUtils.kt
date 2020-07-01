package de.axelrindle.pocketknife.util

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Utilities for working with [Location]s.
 */
object LocationUtils {

    private const val INDEX_WORLD = 0
    private const val INDEX_COORD_X = 1
    private const val INDEX_COORD_Y = 2
    private const val INDEX_COORD_Z = 3
    private const val INDEX_PITCH = 4
    private const val INDEX_YAW = 5

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
        val world = if (list[INDEX_WORLD] == "null") null else Bukkit.getWorld(list[0])
        return Location(world,
            list[INDEX_COORD_X].toDouble(), list[INDEX_COORD_Y].toDouble(), list[INDEX_COORD_Z].toDouble(),
            list[INDEX_PITCH].toFloat(), list[INDEX_YAW].toFloat())
    }
}