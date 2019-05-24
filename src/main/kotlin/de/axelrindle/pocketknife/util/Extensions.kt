package de.axelrindle.pocketknife.util

import org.bukkit.command.CommandSender

/**
 * Provides useful extension functions.
 */
@Suppress("unused")
object Extensions {

    /**
     * Sends a color formatted message.
     *
     * @param message The message to send.
     */
    fun CommandSender.sendMessageF(message: String) {
        this.sendMessage(ChatUtils.formatColors(message))
    }
}