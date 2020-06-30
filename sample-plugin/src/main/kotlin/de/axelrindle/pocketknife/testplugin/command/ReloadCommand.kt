package de.axelrindle.pocketknife.testplugin.command

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.testplugin.PocketKnifeTestPlugin
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ReloadCommand(
        private val plugin: PocketKnifeTestPlugin
) : PocketCommand() {

    override fun getName(): String {
        return "reload"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("Commands.Reload")!!
    }

    override fun getPermission(): String {
        return "pocketknife.reload"
    }

    override fun getUsage(): String {
        return "/pocketknife reload"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        plugin.config.reloadAll()
        val msg = plugin.localization.localize("Messages.Reload")!!
        sender.sendMessageF(msg)
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