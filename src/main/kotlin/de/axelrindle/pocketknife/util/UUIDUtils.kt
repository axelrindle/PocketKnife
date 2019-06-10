package de.axelrindle.pocketknife.util

import de.axelrindle.pocketknife.tasks.RetrieveUUIDTask
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

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

    /**
     * Lookup the uuid for the given username.
     *
     * @param name The username.
     * @param callback A callback which consumes the fetched [UUID], or null on error.
     */
    fun lookup(name: String, callback: Consumer<in UUID?>) {
        val runnable = RetrieveUUIDTask(name)
        val future = CompletableFuture.supplyAsync(runnable::run)
        future.thenAccept(callback)
    }

    /**
     * Synchronous version of [lookup].
     *
     * @param name The username.
     * @return The fetched [UUID] or null on error.
     */
    fun lookupSync(name: String): UUID? {
        return RetrieveUUIDTask(name).run()
    }
}