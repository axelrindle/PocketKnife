package de.axelrindle.pocketknife

import de.axelrindle.pocketknife.util.InventoryUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * A helper class for easy management of inventories.
 *
 * @param plugin A [JavaPlugin] instance.
 * @param name The name of this inventory. May contain color codes.
 * @param size The size of this inventory. Must be a multiple of 9 and between 1 and 6.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class PocketInventory(
        plugin: JavaPlugin,
        val name: String,
        val size: Int
) {

    private val listener: PocketInventory.ClickListener
    private val itemList: HashMap<Int, ItemStack> = HashMap()
    private val clickListeners: HashMap<Int, (event: InventoryClickEvent) -> Unit> = HashMap()

    init {
        // minimum and maximum size
        if (size < 1 || size > 6)
            throw IllegalArgumentException("size must be between 1 and 6, got '$size'!")

        // register event listener
        listener = ClickListener(this)
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }

    /**
     * Adds a new item to the inventory.
     *
     * @param position The position of the item in the inventory.
     * @param stack The [ItemStack] to add.
     * @param handler A click listener called when this item is clicked. Can be omitted.
     *
     * @see InventoryUtils.getIndex
     */
    fun setItem(position: Int, stack: ItemStack, handler: ((event: InventoryClickEvent) -> Unit)? = null) {
        // validate position
        if (position < 0 || position > 53)
            throw IllegalArgumentException("position must be between 0 and 53, got '$position'!")

        itemList[position] = stack
        if (handler == null) return
        clickListeners[position] = handler
    }

    /**
     * Removes an item.
     *
     * @param position The index of the item to remove.
     */
    fun removeItem(position: Int) {
        itemList.remove(position)
        clickListeners.remove(position)
    }

    /**
     * Open this inventory for a player.
     *
     * @param player The [Player] who should see the inventory.
     */
    fun open(player: Player) {
        val inv = Bukkit.createInventory(player, size * 9, name)
        itemList.forEach(inv::setItem)
        player.openInventory(inv)
    }

    /**
     * A [Listener] which listens for inventory events.
     */
    private class ClickListener(
            private val pocketInventory: PocketInventory
    ) : Listener {

        /**
         * Handles a click event on an item. Invokes the appropriate listener
         * from the [pocketInventory].
         *
         * @param e An [InventoryClickEvent] instance.
         */
        @EventHandler
        fun onItemClicked(e: InventoryClickEvent) {
            e.isCancelled = true
            pocketInventory.clickListeners[e.slot]?.invoke(e)
        }
    }
}