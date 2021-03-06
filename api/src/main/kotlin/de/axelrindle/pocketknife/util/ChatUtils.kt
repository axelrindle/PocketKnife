package de.axelrindle.pocketknife.util

import org.bukkit.ChatColor

/**
 * Utility methods for the ingame chat.
 */
object ChatUtils {

    /**
     * Formats a message to enable display of colors in the chat.
     *
     * @param message The message to format.
     * @return The color-enabled message.
     *
     * @see ChatColor.translateAlternateColorCodes
     */
    fun formatColors(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}