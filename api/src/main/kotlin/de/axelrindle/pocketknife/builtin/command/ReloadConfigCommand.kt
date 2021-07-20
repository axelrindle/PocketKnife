package de.axelrindle.pocketknife.builtin.command

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * A reload command is a common practice which allows users to reload
 * a configuration file at runtime.
 *
 * @since 2.2.0
 */
open class ReloadConfigCommand(
    private val plugin: JavaPlugin,
    private val config: PocketConfig
) : PocketCommand() {

    /**
     * Called right before the reload is executed. Use this for example
     * to stop dependent tasks which depend on the configuration.
     */
    open fun onPreReload() {}

    /**
     * Called once when a user reloads all config files at once.
     */
    open fun onReloadAll() {}

    /**
     * Called once for each config file that is reloaded individually.
     *
     * @param which The name of the config file which was reloaded.
     */
    open fun onReload(which: String) {}

    /**
     * Called when an invalid config name has been supplied.
     *
     * @param which The invalid config name.
     */
    open fun onInvalid(which: String) {}

    override fun getName(): String {
        return "reload"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            onPreReload()
            config.reloadAll()
            onReloadAll()
        } else {
            args.forEach {
                onPreReload()
                try {
                    config.reload(it)
                    onReload(it)
                } catch (e: IllegalArgumentException) {
                    onInvalid(it)
                    plugin.logger.log(Level.SEVERE, "Failed reloading the config file \"$it\"!", e)
                }
            }
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, command: Command, args: Array<out String>): MutableList<String> {
        val all = config.list()
        if (args.isEmpty()) return all
        else return all.filter { element ->
            for (arg in args) {
                if (element.contains(arg)) {
                    return@filter true
                }
            }
            return@filter false
        } as MutableList<String>
    }
}