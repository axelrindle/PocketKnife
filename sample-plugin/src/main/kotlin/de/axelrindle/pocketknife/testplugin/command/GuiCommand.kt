package de.axelrindle.pocketknife.testplugin.command

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketInventory
import de.axelrindle.pocketknife.testplugin.PocketKnifeTestPlugin
import de.axelrindle.pocketknife.util.InventoryUtils
import de.axelrindle.pocketknife.util.updateMeta
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

class GuiCommand(
        private val plugin: PocketKnifeTestPlugin
) : PocketCommand() {

    private val pocketInventory =
            PocketInventory(plugin, "§l" + plugin.localization.localize("Titles.Gui")!!, 6)

    init {
        val pane = InventoryUtils.makeStack(Material.BLUE_STAINED_GLASS_PANE, "-")
        for (x in 1..9) {
            for (y in 1..6) {
                if (x == 1 || x == 9 || y == 1 || y == 6) {
                    val index = InventoryUtils.getIndex(x, y)
                    pocketInventory.setItem(index, pane)
                }
            }
        }

        // player skull
        val skullLore = plugin.localization.localize("Messages.You")!!
        val skull = InventoryUtils.makeStack(Material.PLAYER_HEAD, "NAME HERE", skullLore)
        pocketInventory.setItem(InventoryUtils.getIndex(5, 3), skull)

        val item = InventoryUtils.makeStack(Material.BLUE_BED, "Yeet")
        pocketInventory.setItem(InventoryUtils.getIndex(5, 4), item) {
            val player = it.whoClicked as Player
            player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
        }
    }

    override fun getName(): String {
        return "gui"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("Commands.Gui")!!
    }

    override fun getPermission(): String? {
        return "pocketknife.gui"
    }

    override fun getUsage(): String {
        return "/pocketknife gui"
    }

    override fun requirePlayer(): Boolean {
        return true
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        pocketInventory.open(sender as Player) {
            it.getItem(InventoryUtils.getIndex(5, 3))?.updateMeta {
                (this as SkullMeta).owningPlayer = sender
                setDisplayName(sender.displayName)
            }
        }
        return true
    }

    override fun messageNoMatch(input: String): String {
        return plugin.localization.localize("Errors.NoMatch", input)!!
    }

    override fun messageNoPermission(): String? {
        return plugin.localization.localize("Errors.NoPermission")!!
    }

    override fun messageNoPlayer(sender: CommandSender): String {
        return plugin.localization.localize("Errors.PlayerRequired")!!
    }
}