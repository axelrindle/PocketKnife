package de.axelrindle.pocketknife

import de.axelrindle.pocketknife.util.Extensions.sendMessageF
import org.bukkit.command.*
import org.bukkit.entity.Player
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
            pocketCommand.pluginCommand = pluginCommand!!
            pocketCommand.register()
        }
    }

    private lateinit var pluginCommand: PluginCommand

    private fun register() {
        this.pluginCommand.apply {
            this.setExecutor(this@PocketCommand)
            this.tabCompleter = this@PocketCommand
        }
    }


    // # # # # # # # # # # # #
    // Custom Implementation
    //

    final override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>):
            Boolean {
        if (hasSubCommands() && args.isNotEmpty()) {
            val subs = getSubCommands()
            val subName = args[0]
            var found = false

            // try to find a matching sub-command
            for (sub in subs) {
                if (sub.getName().toLowerCase() == subName.toLowerCase()) {
                    found = true
                    if (sub.testPermission(sender)) {
                        return sub.onCommand(sender, command, label,
                                if (args.isNotEmpty()) args.copyOfRange(1, args.size) else args)
                    }
                }
            }

            // send "no match" message
            if (!found && !canBeHandledWhenNoMatch()) {
                sender.sendMessageF(messageNoMatch(subName))
                return false
            }
        }

        // if a player is required, check for him
        if (requirePlayer() && sender !is Player) {
            sender.sendMessage(messageNoPlayer(sender))
            return true
        }

        // nothing returned; handle normally
        return handle(sender, command, args)
    }

    /**
     * @see TabCompleter.onTabComplete
     */
    final override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>):
            MutableList<String> {
        return if (args.size == 1) { // top level completion
            val list = ArrayList<String>()

            if (hasSubCommands()) {
                getSubCommands().forEach {
                    if (it.getName().contains(args[0], true)) {
                        list += it.getName()
                    }
                }
            }
            if (!hasSubCommands() || canBeHandledWhenNoMatch()) {
                list.addAll(tabComplete(sender, command, args))
            }

            list
        }
        else { // recursive deep completion
            val list = ArrayList<String>()
            getSubCommands()
                    .stream()
                    .filter { it.getName().equals(args[0], true) }
                    .findFirst()
                    .ifPresent {
                        list.addAll(it.onTabComplete(sender, command, alias, args.copyOfRange(1, args.size)))
                    }
            list
        }
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

    open fun tabComplete(sender: CommandSender, command: Command, args: Array<out String>):
            MutableList<String> {
        return ArrayList()
    }

    /**
     * Sends a help message to the sender.
     *
     * @param sender Source of the command.
     */
    open fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${getUsage()} - ${getDescription()}")
    }

    /**
     * @return An [ArrayList] of sub-commands.
     */
    open fun getSubCommands(): ArrayList<PocketCommand> {
        return ArrayList()
    }

    /**
     * @return Whether a [Player] is required to execute this command.
     */
    open fun requirePlayer(): Boolean {
        return false
    }

    /**
     * @return Whether this command should be handled normally via [handle] when no matching
     *         sub-command is found.
     */
    open fun canBeHandledWhenNoMatch(): Boolean {
        return false
    }

    /**
     * @return A message shown to the user when no matching sub-command was found.
     */
    open fun messageNoMatch(input: String): String {
        return "Nothing matched '$input'!"
    }

    /**
     * @return A message shown to the user when he doesn't have permission to execute a command.
     */
    open fun messageNoPermission(): String? {
        return "You don't have permission to execute this command!"
    }

    /**
     * @return A message shown when the executing [CommandSender] is not a [Player].
     */
    open fun messageNoPlayer(sender: CommandSender): String {
        return "A player is required to execute this command!"
    }

    /**
     * This should be overridden by sub-commands.
     *
     * @see PluginCommand.getDescription
     */
    open fun getDescription(): String {
        return pluginCommand.description
    }

    /**
     * This should be overridden by sub-commands.
     *
     * @see PluginCommand.getUsage
     */
    open fun getUsage(): String {
        return pluginCommand.usage
    }

    /**
     * This should be overridden by sub-commands.
     *
     * @see PluginCommand.getPermission
     */
    open fun getPermission(): String? {
        return pluginCommand.permission
    }

    /**
     * Tests the given [CommandSender] to see if they can perform this
     * command.
     *
     * If they do not have permission, they will be informed that they cannot
     * do this.
     *
     * @param target User to test
     * @see PluginCommand.testPermission
     */
    fun testPermission(target: CommandSender): Boolean {
        if (testPermissionSilent(target)) {
            return true
        }

        if (messageNoPermission() == null) {
            target.sendMessageF("&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.")
        } else if (messageNoPermission()?.length != 0) {
            for (line in messageNoPermission()!!
                    .replace("<permission>", getPermission()!!)
                    .split("\n")) {
                target.sendMessageF(line)
            }
        }

        return false
    }

    /**
     * Tests the given [CommandSender] to see if they can perform this
     * command.
     *
     * No error is sent to the sender.
     *
     * @param target User to test
     * @return true if they can use it, otherwise false
     * @see PluginCommand.testPermissionSilent
     */
    fun testPermissionSilent(target: CommandSender): Boolean {
        if (getPermission() == null || getPermission()?.isEmpty()!!) {
            return true
        }

        for (p in getPermission()!!.split(";")) {
            if (target.hasPermission(p)) {
                return true
            }
        }

        return false
    }


    // # # # # # # # # # # # #
    // Private Stuff
    //

    private fun hasSubCommands(): Boolean {
        return getSubCommands().size > 0
    }
}