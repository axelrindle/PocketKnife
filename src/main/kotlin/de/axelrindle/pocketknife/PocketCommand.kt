package de.axelrindle.pocketknife

import org.bukkit.command.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * An abstract [CommandExecutor] implementation with support for sub-commands,
 * automated help and tab-completion.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "CanBeParameter")
abstract class PocketCommand : CommandExecutor, TabCompleter {

    // # # # # # # # # # # # #
    // Initialization
    //

    companion object {

        /**
         * Registers a [PocketCommand] to be the [CommandExecutor] for a [PluginCommand].
         *
         * @param plugin The executing [JavaPlugin].
         * @param pocketCommand The [PocketCommand] instance.
         */
        fun register(plugin: JavaPlugin, pocketCommand: PocketCommand) {
            val pluginCommand = plugin.getCommand(pocketCommand.getName())
            pocketCommand.pluginCommand = pluginCommand
            pocketCommand.register()
        }
    }

    private lateinit var pluginCommand: PluginCommand

    private fun register() {
        this.pluginCommand.apply {
            this.executor = this@PocketCommand
            this.tabCompleter = this@PocketCommand
        }
    }


    // # # # # # # # # # # # #
    // Custom Implementation
    //

    final override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>):
            Boolean {

        if (hasSubCommands()) {
            val subs = getSubCommands()
            val subName = args[1]
            var found = false

            // try to find a matching sub-command
            for (sub in subs) {
                if (sub.getName() == subName) {
                    sub.onCommand(sender, command, label, if (args.size >= 2) args.copyOfRange(2, args.size) else args)
                    found = true
                    break
                }
            }

            // no matches, send a message
            if (!found) {
                sender.sendMessage(messageNoMatch(subName))
                return false
            }
        } else {
            return handle(sender, command, if (args.size >= 1) args.copyOfRange(1, args.size) else args)
        }

        return true
    }

    /**
     * @see TabCompleter.onTabComplete
     */
    final override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>):
            MutableList<String> {
        // TODO: Implement fully functional tab-completion with support for sub-commands
        return ArrayList()
    }


    // # # # # # # # # # # # # #
    // Abstract/Inherited Stuff
    //

    /**
     * @return The name of this command.
     */
    abstract fun getName(): String

    /**
     * The command handler.
     *
     * @param sender Source of the command.
     * @param command Command which was executed.
     * @param args Passed command arguments.
     *
     * @return `true` if a valid command, otherwise `false`.
     * @see CommandExecutor.onCommand
     */
    open fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        sendHelp(sender)
        return true
    }

    /**
     * Sends a help message to the sender.
     *
     * @param sender Source of the command.
     */
    abstract fun sendHelp(sender: CommandSender)

    /**
     * @return An [ArrayList] of sub-commands.
     */
    open fun getSubCommands(): ArrayList<PocketCommand> {
        return ArrayList()
    }

    /**
     * @return A message shown to the user when no matching sub-command was found.
     */
    open fun messageNoMatch(input: String): String {
        return "Nothing matched '$input'!"
    }

    /**
     * @see PluginCommand.getDescription
     */
    fun getDescription(): String {
        return pluginCommand.description
    }

    /**
     * @see PluginCommand.getUsage
     */
    fun getUsage(): String {
        return pluginCommand.usage
    }

    /**
     * @see PluginCommand.getPermission
     */
    fun getPermission(): String {
        return pluginCommand.permission
    }

    /**
     * @see PluginCommand.testPermission
     */
    fun testPermission(target: CommandSender): Boolean {
        return pluginCommand.testPermission(target)
    }

    /**
     * @see PluginCommand.testPermissionSilent
     */
    fun testPermissionSilent(target: CommandSender): Boolean {
        return pluginCommand.testPermissionSilent(target)
    }


    // # # # # # # # # # # # #
    // Private Stuff
    //

    private fun hasSubCommands(): Boolean {
        return getSubCommands().size > 0
    }
}