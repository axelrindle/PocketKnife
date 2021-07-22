package de.axelrindle.pocketknife

import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * An abstract [CommandExecutor] implementation with support for sub-commands,
 * automated help and tab-completion.
 *
 * @since 1.0.0
 */
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
        @JvmStatic
        fun register(plugin: JavaPlugin, pocketCommand: PocketCommand) {
            val pluginCommand = plugin.getCommand(pocketCommand.getName())!!
            pocketCommand.pluginCommand = pluginCommand.apply {
                setExecutor(pocketCommand)
                tabCompleter = pocketCommand
            }
        }
    }

    private lateinit var pluginCommand: PluginCommand

    // # # # # # # # # # # # #
    // Custom Implementation
    //

    /**
     * @see CommandExecutor.onCommand
     */
    final override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>):
            Boolean {
        // make sure the sender has the required permission
        if (!testPermission(sender)) return true

        // check for sub-commands
        if (hasSubCommands() && args.isNotEmpty()) {
            val subName = args[0]
            var found = false
            var result = false

            // try to find a matching sub-command
            for (subCommand in subCommands) {
                if (subCommand.getName().equals(subName, ignoreCase = true)) {
                    found = true
                    result = subCommand.onCommand(sender, command, label,
                            if (args.isNotEmpty()) args.copyOfRange(1, args.size) else args)
                    break
                }
            }

            // when the command can't be handled on it's own,
            // handle the result
            if (!canBeHandledWhenNoMatch()) {
                return if (!found) {
                    sender.sendMessageF(messageNoMatch(subName))
                    true
                } else {
                    result
                }
            }
        }

        // if a player is required, check for him
        if (requirePlayer() && sender !is Player) {
            sender.sendMessageF(messageNoPlayer(sender))
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
        val list = ArrayList<String>()
        if (args.size <= 1) { // top level completion

            if (hasSubCommands()) {
                subCommands.forEach {
                    if (it.testPermissionSilent(sender) &&
                        (args.isEmpty() || it.getName().contains(args[0], true))
                    ) {
                        list += it.getName()
                    }
                }
            }
            if (testPermissionSilent(sender) && (!hasSubCommands() || canBeHandledWhenNoMatch())) {
                list.addAll(tabComplete(sender, command, args))
            }
        }
        else { // recursive deep completion
            subCommands
                    .stream()
                    .filter { args.isEmpty() || it.getName().equals(args[0], true) }
                    .filter { it.testPermissionSilent(sender) }
                    .findFirst()
                    .ifPresent {
                        list.addAll(it.onTabComplete(sender, command, alias, args.copyOfRange(1, args.size)))
                    }
        }
        return list
    }

    // # # # # # # # # # # # # #
    // Abstract/Inherited Stuff
    //

    /**
     * Defines the list of available sub-commands.
     */
    protected open val subCommands: ArrayList<PocketCommand> = ArrayList()

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
     * Requests a list of possible completions for a command argument.
     *
     * @param sender Source of the command.  For players tab-completing a
     *               command inside of a command block, this will be the player, not
     *               the command block.
     * @param command Command which was executed
     * @param args The arguments passed to the command, including final
     *             partial argument to be completed and command label.
     *
     * @return A list of possible completions for the final argument, or null
     *         to default to the command executor.
     */
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
        subCommands.forEach { it.sendHelp(sender) }
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
    @Suppress("MemberVisibilityCanBePrivate")
    fun testPermissionSilent(target: CommandSender): Boolean {
        if (getPermission().isNullOrBlank()) {
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
        return subCommands.size > 0
    }
}