import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock
import de.axelrindle.pocketknife.PocketCommand
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.bukkit.ChatColor
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

    "testPermissionSilent returns true when a player has at least one out of multiple permissions" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun getPermission(): String {
                return "test.perm1;test.perm2"
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        val player1 = MockBukkit.getMock().getPlayer(1)
        player1.addAttachment(mockedPlugin, "test.perm1", true)

        val player2 = MockBukkit.getMock().getPlayer(2)
        player2.addAttachment(mockedPlugin, "test.perm2", true)

        pocketCommand.testPermissionSilent(player1) shouldBe true
        pocketCommand.testPermissionSilent(player2) shouldBe true
    }

    "testPermissionSilent returns false for missing permission(s)" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun getPermission(): String {
                return "test.perm1;test.perm2"
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        pocketCommand.testPermissionSilent(player) shouldBe false
    }

    "testPermissionSilent returns true for operators" {
        val op = MockBukkit.getMock().getPlayer(9)
        op.isOp = true

        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun getPermission(): String {
                return "test.perm1;test.perm2"
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        pocketCommand.testPermissionSilent(op) shouldBe true
    }

    "testPermission returns false for missing permission(s)" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun getPermission(): String {
                return "test.perm1;test.perm2"
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        pocketCommand.testPermission(player) shouldBe false
    }

    "testPermission sends the correct messages to the sender" {
        var sendCustom = false
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override fun getPermission(): String {
                return "test.perm1;test.perm2"
            }

            override fun messageNoPermission(): String? {
                return if (sendCustom) "You shall not pass!"
                else null
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        val player3 = MockBukkit.getMock().getPlayer(3)

        pocketCommand.testPermission(player3) shouldBe false
        ChatColor.stripColor(player3.nextMessage()) shouldBe "I'm sorry, but you do not have permission to perform " +
                "this command. Please contact the server administrators if you believe that this is in error."

        sendCustom = true
        pocketCommand.testPermission(player3) shouldBe false
        player3.nextMessage() shouldBe "You shall not pass!"
    }

    "tab completion returns nothing for empty implementation" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        pocketCommand.onTabComplete(player, pluginCommand, pocketCommand.getName(), emptyArray()) should beEmpty()
    }

    "tab completion completes sub commands" {
        val pocketCommand = object : PocketCommand() {
            override fun getName(): String {
                return "test"
            }

            override val subCommands: ArrayList<PocketCommand> = arrayListOf(
                object : PocketCommand() {
                    override fun getName(): String = "subCommand1"
                    override fun getPermission(): String? = null
                },
                object : PocketCommand() {
                    override fun getName(): String = "subCommand2"
                    override fun getPermission(): String? = null
                },
                object : PocketCommand() {
                    override fun getName(): String = "anotherOne"
                    override fun getPermission(): String? = null
                }
            )
        }
        PocketCommand.register(mockedPlugin, pocketCommand)

        val emptyArray = pocketCommand.onTabComplete(player, pluginCommand, pocketCommand.getName(), emptyArray())
        emptyArray shouldHaveSize 3
        emptyArray shouldContainExactly arrayListOf("subCommand1", "subCommand2", "anotherOne")

        val sArray = pocketCommand.onTabComplete(player, pluginCommand, pocketCommand.getName(), arrayOf("s"))
        sArray shouldHaveSize 2
        sArray shouldContainExactly arrayListOf("subCommand1", "subCommand2")

        val anoArray = pocketCommand.onTabComplete(player, pluginCommand, pocketCommand.getName(), arrayOf("ano"))
        anoArray shouldHaveSize 1
        anoArray shouldContainExactly arrayListOf("anotherOne")

        val unknownArray = pocketCommand.onTabComplete(player, pluginCommand, pocketCommand.getName(), arrayOf("unknown"))
        unknownArray should beEmpty()
    }

})