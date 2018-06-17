package de.axelrindle.pocketknife.util

import de.axelrindle.pocketknife.PocketInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Utility methods for use with the [PocketInventory] class.
 */
@Suppress("unused")
object InventoryUtils {

    private val INDEX_MATRIX = Array(9) { Array(6) { it } }

    init {
        var i = 0
        for (y in 0..5)
            for (x in 0..8)
                INDEX_MATRIX[x][y] = i++
    }

    /**
     * Returns an inventory index from the given matrix.
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
        if (column < 1 || column > 9)
            throw IllegalArgumentException("column index must be between 1 and 9, got '$column'!")

        // validate row index
        if (row < 1 || row > 6)
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
        val meta = stack.itemMeta
        meta.displayName = name
        meta.lore = lore.toList()
        stack.itemMeta = meta
        return stack
    }
}