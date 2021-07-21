package de.axelrindle.testplugin.command

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.testplugin.PocketKnifeTestPlugin
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.CommandSender

class PocketKnifeCommand(
        private val plugin: PocketKnifeTestPlugin
) : PocketCommand() {

    override val subCommands = arrayListOf(
            ReloadCommand(plugin, plugin.config),
            GuiCommand(plugin),
            NotesCommand(plugin)
    )

    override fun getName(): String {
        return "pocketknife"
    }

    override fun sendHelp(sender: CommandSender) {
        val helpText = plugin.localization.localize("Titles.Help")!!
        sender.sendMessageF(helpText)
        var i = 0
        if (testPermission(sender)) {
            sender.sendMessageF("${getUsage()} - ${getDescription()}")
            i++
        }
        subCommands.forEach {
            if (it.getPermission() == null || sender.hasPermission(it.getPermission()!!)) {
                it.sendHelp(sender)
                i++
            }
        }

        // nothing sent, send no permission message
        if (i == 0) messageNoPermission()?.let { sender.sendMessage(it) }
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