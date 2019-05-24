package de.axelrindle.pocketknife.util

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Utilities for working with [Location]s.
 */
@Suppress("unused")
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
        return pos.world!!.name + SEPARATOR + list.joinToString(SEPARATOR)
    }

    /**
     * Parses a [String] into a [Location].
     *
     * @param s The serialized [Location].
     * @return The
     */
    fun deserializeLocation(s: String): Location {
        val list = s.split(SEPARATOR)
        return Location(Bukkit.getWorld(list[0]),
            list[1].toDouble(), list[2].toDouble(), list[3].toDouble(),
            list[4].toFloat(), list[5].toFloat())
    }
}