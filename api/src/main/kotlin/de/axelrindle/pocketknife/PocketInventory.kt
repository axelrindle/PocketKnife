package de.axelrindle.pocketknife

import de.axelrindle.pocketknife.util.InventoryUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * A helper class for easy management of inventories.
 *
 * @param plugin A [JavaPlugin] instance.
 * @param name The name of this inventory. May contain color codes.
 * @param size The size of this inventory. Must be a multiple of 9 and between 1 and 6.
 *
 * @since 1.0.0
 */
@Suppress("MemberVisibilityCanBePrivate")
class PocketInventory(
        plugin: JavaPlugin,
        val name: String,
        val size: Int
) {

    companion object {
        /** An [Inventory] must contain at least one row. */
        const val MIN_INVENTORY_SIZE = 1

        /** An [Inventory] must contain a maximum of 6 rows. */
        const val MAX_INVENTORY_SIZE = 6

        /** One [Inventory] row consists of 9 slots. */
        const val INVENTORY_ROW_SIZE = 9
    }

    private val listener = ClickListener(this)
    private val itemList: HashMap<Int, ItemStack> = HashMap()
    private val clickListeners: HashMap<Int, (event: InventoryClickEvent) -> Unit> = HashMap()

    init {
        // minimum and maximum size
        if (size < MIN_INVENTORY_SIZE || size > MAX_INVENTORY_SIZE)
            throw IllegalArgumentException("size must be between 1 and 6, got '$size'!")

        // register event listener
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
        val max = size * INVENTORY_ROW_SIZE - 1
        if (position < 0 || position > max)
            throw IllegalArgumentException("position must be between 0 and $max, got '$position'!")

        itemList[position] = stack
        if (handler == null) return
        clickListeners[position] = handler
    }

    /**
     * Retrieves the [ItemStack] from the given inventory position.
     *
     * @param position The position of the item in the inventory.
     * @return An [ItemStack] or `null` if not found.
     */
    fun getItem(position: Int): ItemStack? {
        return itemList[position]
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
     * @param consumer An optional callback that can be used to modify the
     *                 created [Inventory] instance before the [Player] sees it.
     */
    fun open(player: Player, consumer: ((inv: Inventory) -> Unit)? = null) {
        val inv = Bukkit.createInventory(player, size * INVENTORY_ROW_SIZE, name)
        itemList.forEach(inv::setItem)
        consumer?.invoke(inv)
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
            if (e.view.title != pocketInventory.name) return

            pocketInventory.clickListeners[e.slot]?.invoke(e)
            e.isCancelled = true
        }
    }
}