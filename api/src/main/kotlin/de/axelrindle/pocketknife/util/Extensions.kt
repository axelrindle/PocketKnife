@file:Suppress("unused")

package de.axelrindle.pocketknife.util

import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Sends a color formatted message.
 *
 * @param message The message to send.
 */
fun CommandSender.sendMessageF(message: String) {
    this.sendMessage(ChatUtils.formatColors(message))
}

/**
 * Shorthand method for updating an [ItemStack]s [ItemMeta].
 */
fun ItemStack.updateMeta(block: ItemMeta.() -> Unit) {
    this.itemMeta = this.itemMeta!!.apply(block)
}