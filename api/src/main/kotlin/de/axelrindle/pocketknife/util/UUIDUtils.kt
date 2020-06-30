package de.axelrindle.pocketknife.util

import java.util.*

/**
 * Utilities for working with [UUID]s.
 */
object UUIDUtils {

    /**
     * @return Whether the given string is a valid [UUID].
     * @see UUID.fromString
     */
    fun isValid(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * Converts a trimmed [UUID] back to a full [UUID].
     * Note: a trimmed UUID has no dashes, where a full UUID has them.
     *
     * @throws StringIndexOutOfBoundsException the given uuid string is invalid.
     */
    fun trimmedToFull(uuid: String): String {
        return buildString {
            append(uuid)
            insert(8, "-")
            insert(13, "-")
            insert(18, "-")
            insert(23, "-")
        }
    }
}