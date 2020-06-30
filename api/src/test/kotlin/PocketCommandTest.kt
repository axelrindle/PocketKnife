import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock
import de.axelrindle.pocketknife.PocketCommand
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PocketCommandTest : StringSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.load(CustomMockPlugin::class.java)
    val pluginCommand = mockedPlugin.getCommand("test")!!
    val player = MockBukkit.getMock().getPlayer(0)
    val console = ConsoleCommandSenderMock()

    "top-level command should send help" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun getUsage(): String {
                return "/test"
            }

            override fun getDescription(): String {
                return "Test command"
            }

            override fun getPermission(): String? {
                return null
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        val result = pocketCommand.onCommand(player, pluginCommand, "test", emptyArray())
        result shouldBe true
        player.nextMessage() shouldBe "${pocketCommand.getUsage()} - ${pocketCommand.getDescription()}"
    }

    "top-level command should require a player" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun requirePlayer(): Boolean {
                return true
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        val result = pocketCommand.onCommand(console, pluginCommand, "test", emptyArray())
        result shouldBe true
        console.nextMessage() shouldBe "A player is required to execute this command!"
    }

    "sub-commands are handled" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun canBeHandledWhenNoMatch(): Boolean {
                return true
            }

            override val subCommands: ArrayList<PocketCommand> = arrayListOf(
                    object : PocketCommand() {
                        override fun getName(): String {
                            return "subtest"
                        }

                        override fun getDescription(): String {
                            return "Subcommand test"
                        }

                        override fun getUsage(): String {
                            return "/test subtest"
                        }

                        override fun getPermission(): String? {
                            return null
                        }

                        override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
                            sender.sendMessage("Test from SubCommand")
                            return true
                        }
                    }
            )

            override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
                sender.sendMessage("Test from normal handle")
                return true
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        val resultForSubCommand = pocketCommand.onCommand(player, pluginCommand, "test", arrayOf("subtest"))
        resultForSubCommand shouldBe true
        player.nextMessage() shouldBe "Test from SubCommand"

        val resultForTopCommand = pocketCommand.onCommand(player, pluginCommand, "test", emptyArray())
        resultForTopCommand shouldBe true
        player.nextMessage() shouldBe "Test from normal handle"
    }

})