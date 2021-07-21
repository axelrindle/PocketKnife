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
open class ReloadConfigCommand<T>(
    protected val plugin: T,
    protected val config: PocketConfig
) : PocketCommand() where T : JavaPlugin {

    /**
     * Represents events in the command lifecycle.
     */
    enum class Event {
        /**
         * Occurs before anything is done. Use this to stop tasks depending on config files.
         */
        PRE_RELOAD,

        /**
         * Occurs after the config files have been reloaded and no error occurred.
         */
        AFTER_RELOAD,

        /**
         * Called for every single config file that is being reloaded.
         */
        SINGLE,

        /**
         * Indicates that an invalid config name has been supplied by the user.
         */
        INVALID,

        /**
         * Indicates that an unexpected error has occurred (e.g. an [java.io.IOException]).
         */
        ERROR
    }

    /**
     * Indicates whether users can reload single config files instead of all.
     */
    protected open val canReloadSingle: Boolean = true

    /**
     * Called for each event in the command lifecycle.
     *
     * @param event The [Event] that occurred.
     * @param sender The [CommandSender] which initiated the reload.
     * @param info An optional piece of information usually indicating which config files
     *             is being reloaded.
     * @param error An optional [Throwable] object supplied if an unexpected error occurs.
     *
     * @see Event
     */
    open fun onEvent(event: Event, sender: CommandSender, info: String? = null, error: Throwable? = null) {
        if (error != null) {
            plugin.logger.log(Level.SEVERE, "Failed reloading the config file \"$info\"!", error)
        }
    }

    override fun getName(): String {
        return "reload"
    }

    override fun getUsage(): String {
        return if (canReloadSingle) "/${getName()} [config]"
        else "/" + getName()
    }

    final override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        onEvent(Event.PRE_RELOAD, sender)

        return when {
            args.isEmpty() -> {
                try {
                    config.reloadAll()
                    onEvent(Event.AFTER_RELOAD, sender)
                } catch (e: IllegalArgumentException) {
                    onEvent(Event.INVALID, sender, null)
                } catch (e: Exception) {
                    onEvent(Event.ERROR, sender, null, e)
                }
                true
            }
            canReloadSingle -> {
                runCatching {
                    for (it in args) {
                        config.reload(it)
                        onEvent(Event.SINGLE, sender, it)
                    }
                }
                    .onSuccess {
                        onEvent(Event.AFTER_RELOAD, sender)
                    }
                    .onFailure { exception ->
                        if (exception is IllegalArgumentException) {
                            val notApostrophe: (Char) -> Boolean = { it != '\'' }
                            val which = exception.message // extract config name from exception message
                                ?.dropWhile(notApostrophe)
                                ?.dropLastWhile(notApostrophe)
                                ?.drop(1)
                                ?.dropLast(1)
                            onEvent(Event.INVALID, sender, which, exception)
                        } else {
                            onEvent(Event.ERROR, sender, null, exception)
                        }
                    }
                true
            }
            else -> false
        }
    }

    final override fun tabComplete(sender: CommandSender, command: Command, args: Array<out String>): MutableList<String> {
        if (canReloadSingle.not()) return arrayListOf()

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