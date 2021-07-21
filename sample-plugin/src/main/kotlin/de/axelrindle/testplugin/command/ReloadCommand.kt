package de.axelrindle.testplugin.command

import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.builtin.command.ReloadConfigCommand
import de.axelrindle.pocketknife.util.sendMessageF
import de.axelrindle.testplugin.PocketKnifeTestPlugin
import org.bukkit.command.CommandSender

class ReloadCommand(plugin: PocketKnifeTestPlugin, config: PocketConfig) :
    ReloadConfigCommand<PocketKnifeTestPlugin>(plugin, config) {

    override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
        super.onEvent(event, sender, info, error)
        when(event) {
            Event.PRE_RELOAD -> sender.sendMessageF("Dependent tasks have been stopped.")
            Event.AFTER_RELOAD -> sender.sendMessageF("Dependent tasks have been restarted.")
            Event.INVALID -> sender.sendMessageF("&c${error!!.message}")
            Event.ERROR -> sender.sendMessageF("&cAn error occurred! Please check the console for details.")
            else -> {} // ignore everything else
        }
    }

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
        return "/pocketknife reload [config]"
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