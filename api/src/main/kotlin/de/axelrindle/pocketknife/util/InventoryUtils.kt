package de.axelrindle.pocketknife.util

import de.axelrindle.pocketknife.PocketInventory
import de.axelrindle.pocketknife.PocketInventory.Companion.INVENTORY_ROW_SIZE
import de.axelrindle.pocketknife.PocketInventory.Companion.MAX_INVENTORY_SIZE
import de.axelrindle.pocketknife.PocketInventory.Companion.MIN_INVENTORY_SIZE
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Utility methods for use with the [PocketInventory] class.
 */
object InventoryUtils {

    private val INDEX_MATRIX = Array(INVENTORY_ROW_SIZE) { Array(MAX_INVENTORY_SIZE) { it } }

    init {
        var i = 0
        for (y in 0 until MAX_INVENTORY_SIZE)
            for (x in 0 until INVENTORY_ROW_SIZE)
                INDEX_MATRIX[x][y] = i++
    }

    /**
     * Returns an inventory index from the given matrix coordinates.
     *
     * An inventory index matrix looks like this:
     * ```
     * /  |  1  2  3  4  5  6  7  8  9
     * -  |  -- -- -- -- -- -- -- -- --
     * 1  |  0  1  2  3  4  5  6  7  8
     * 2  |  9  10 11 12 13 14 15 16 17
     * 3  |  18 19 20 21 22 23 24 25 26
     * 4  |  27 28 29 30 31 32 33 34 35
     * 5  |  36 37 38 39 40 41 42 43 44
     * 6  |  45 46 47 48 49 50 51 52 53
     * ```
     */
    fun getIndex(column: Int, row: Int): Int {
        // validate column index
        if (column < MIN_INVENTORY_SIZE || column > INVENTORY_ROW_SIZE)
            throw IllegalArgumentException("column index must be between 1 and 9, got '$column'!")

        // validate row index
        if (row < MIN_INVENTORY_SIZE || row > MAX_INVENTORY_SIZE)
            throw IllegalArgumentException("row index must be between 1 and 6, got '$row'!")

        return INDEX_MATRIX[column-1][row-1]
    }

    /**
     * Creates a new [ItemStack].
     *
     * @param material The item [Material] to use.
     * @param name The name of this stack. May contain color codes.
     * @param lore The description of this stack. May contain color codes.
     */
    fun makeStack(material: Material, name: String, vararg lore: String): ItemStack {
        val stack = ItemStack(material, 1)
        stack.updateMeta {
            setDisplayName(name)
            setLore(lore.toList())
        }
        return stack
    }
}