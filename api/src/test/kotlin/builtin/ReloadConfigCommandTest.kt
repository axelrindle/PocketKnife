package builtin

import CustomMockPlugin
import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.builtin.command.ReloadConfigCommand
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe

class ReloadConfigCommandTest : StringSpec({

    val mockedPlugin = MockBukkit.load(CustomMockPlugin::class.java)
    val pocketConfig = PocketConfig(mockedPlugin)
    val sender = ConsoleCommandSenderMock()
    val pluginCommand = mockedPlugin.getCommand("test")!!

    pocketConfig.register("config", mockedPlugin.getResource("/testConfig.yml"))
    pocketConfig.register("anotherConfig", mockedPlugin.getResource("/testConfig.yml"))

    "onReloadAll should be called once" {
        var calls = 0
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onReloadAll() {
                calls++
            }

            override fun onReload(which: String) {
                TODO("Not yet implemented")
            }

            override fun onInvalid(which: String) {
                TODO("Not yet implemented")
            }
        }
        PocketCommand.register(mockedPlugin, command)
        command.onCommand(sender, pluginCommand, pluginCommand.label, emptyArray())

        calls shouldBe 1
    }

    "onReload should be called twice" {
        var calls = 0
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onReloadAll() {
                TODO("Not yet implemented")
            }

            override fun onReload(which: String) {
                calls++
            }

            override fun onInvalid(which: String) {
                TODO("Not yet implemented")
            }
        }
        PocketCommand.register(mockedPlugin, command)

        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config"))
        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("anotherConfig"))

        calls shouldBe 2
    }

    "onInvalid should be called for unknown config names" {
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onReloadAll() {
                TODO("Not yet implemented")
            }

            override fun onReload(which: String) {
                TODO("Not yet implemented")
            }

            override fun onInvalid(which: String) {
                which shouldBe "unknown"
            }
        }
        PocketCommand.register(mockedPlugin, command)
        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("unknown"))
    }

    "tabComplete should behave correctly" {
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onReloadAll() {
                TODO("Not yet implemented")
            }

            override fun onReload(which: String) {
                TODO("Not yet implemented")
            }

            override fun onInvalid(which: String) {
                TODO("Not yet implemented")
            }
        }
        PocketCommand.register(mockedPlugin, command)

        command.tabComplete(sender, pluginCommand, emptyArray())
            .shouldHaveSize(2)
            .shouldBeUnique()
            .shouldContainAll("config", "anotherConfig")
        command.tabComplete(sender, pluginCommand, arrayOf("a")) shouldContainExactly arrayListOf("anotherConfig")
        command.tabComplete(sender, pluginCommand, arrayOf("C")) shouldContainExactly arrayListOf("anotherConfig")
        command.tabComplete(sender, pluginCommand, arrayOf("c")) shouldContainExactly arrayListOf("config")
        command.tabComplete(sender, pluginCommand, arrayOf("x")).shouldBeEmpty()
    }
})