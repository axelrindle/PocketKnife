package builtin

import CustomMockPlugin
import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.builtin.command.ReloadConfigCommand
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
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

    "onPreReload & onReloadAll should be called once" {
        var preCalls = 0
        var calls = 0
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onPreReload() {
                preCalls++
            }

            override fun onReloadAll() {
                calls++
            }
        }
        PocketCommand.register(mockedPlugin, command)
        command.onCommand(sender, pluginCommand, pluginCommand.label, emptyArray())

        preCalls shouldBe 1
        calls shouldBe 1
    }

    "onPreReload & onReload should be called twice" {
        var preCalls = 0
        var calls = 0
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onPreReload() {
                preCalls++
            }

            override fun onReload(which: String) {
                calls++
            }
        }
        PocketCommand.register(mockedPlugin, command)

        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config"))
        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("anotherConfig"))

        preCalls shouldBe 2
        calls shouldBe 2
    }

    "onInvalid should be called for unknown config names while onPreReload gets also called" {
        var preCalled = false
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onPreReload() {
                preCalled = true
            }

            override fun onInvalid(which: String) {
                which shouldBe "unknown"
            }
        }
        PocketCommand.register(mockedPlugin, command)
        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("unknown"))

        preCalled shouldBe true
    }

    "an exception in onPreReload does not lead to onInvalid being called when reloading s single config file" {
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onPreReload() {
                throw RuntimeException("imagine a severe error here")
            }

            override fun onInvalid(which: String) {
                fail("onInvalid should not be called!")
            }
        }
        PocketCommand.register(mockedPlugin, command)

        val thrown = shouldThrow<RuntimeException> {
            command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config"))
        }
        thrown.message shouldBe "imagine a severe error here"
    }

    "an exception in onPreReload does not lead to onInvalid being called when reloading all config files" {
        val command = object : ReloadConfigCommand(mockedPlugin, pocketConfig) {
            override fun onPreReload() {
                throw RuntimeException("imagine a severe error here")
            }

            override fun onInvalid(which: String) {
                fail("onInvalid should not be called!")
            }
        }
        PocketCommand.register(mockedPlugin, command)

        val thrown = shouldThrow<RuntimeException> {
            command.onCommand(sender, pluginCommand, pluginCommand.label, emptyArray())
        }
        thrown.message shouldBe "imagine a severe error here"
    }

    "tabComplete should behave correctly" {
        val command = ReloadConfigCommand(mockedPlugin, pocketConfig)
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