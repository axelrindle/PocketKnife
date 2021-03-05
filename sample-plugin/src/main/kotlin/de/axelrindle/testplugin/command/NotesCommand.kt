package de.axelrindle.testplugin.command

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.testplugin.PocketKnifeTestPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.stream.Collectors

class NotesCommand(
        private val plugin: PocketKnifeTestPlugin
) : PocketCommand() {

    private val notes: HashMap<String, String> = hashMapOf(
            Pair("myNote", "Hello World"),
            Pair("important", "I need DIAMONDS!!!"),
            Pair("test", "test note")
    )

    override fun getName(): String {
        return "notes"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("Commands.Notes")!!
    }

    override fun getPermission(): String? {
        return null
    }

    override fun getUsage(): String {
        return "/pocketknife notes <note>"
    }

    override fun canBeHandledWhenNoMatch(): Boolean {
        return true
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("Available notes: ")
            notes.keys.forEachIndexed { index, s ->
                sender.sendMessage("${index+1}. $s")
            }
        } else {
            val name = args[0]
            val note = notes[name] ?: "No note with key '$name' found!"
            sender.sendMessage(note)
        }

        return true
    }

    override fun tabComplete(sender: CommandSender, command: Command, args: Array<out String>): MutableList<String> {
        val name = args[0]
        return notes.keys.stream()
                .filter { it.contains(name, true) }
                .collect(Collectors.toList())
    }
}