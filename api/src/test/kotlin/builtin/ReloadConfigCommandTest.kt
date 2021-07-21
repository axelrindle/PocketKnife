package builtin

import CustomMockPlugin
import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.builtin.command.ReloadConfigCommand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.bukkit.command.CommandSender
import java.io.IOException

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
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.PRE_RELOAD -> preCalls++
                    Event.AFTER_RELOAD -> calls++
                    else -> org.junit.jupiter.api.fail("Something unexpected happened! ($event)", error)
                }
            }
        }
        PocketCommand.register(mockedPlugin, command)
        command.onCommand(sender, pluginCommand, pluginCommand.label, emptyArray()) shouldBe true

        preCalls shouldBe 1
        calls shouldBe 1
    }

    "onPreReload & onReload should be called twice" {
        var preCalls = 0
        var calls = 0
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.PRE_RELOAD -> preCalls++
                    Event.SINGLE -> {}
                    Event.AFTER_RELOAD -> calls++
                    else -> org.junit.jupiter.api.fail("Something unexpected happened! ($event)", error)
                }
            }
        }
        PocketCommand.register(mockedPlugin, command)

        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config")) shouldBe true
        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("anotherConfig")) shouldBe true

        preCalls shouldBe 2
        calls shouldBe 2
    }

    "onInvalid should be called for unknown config names while onPreReload gets also called" {
        var preCalled = false
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.PRE_RELOAD -> preCalled = true
                    Event.INVALID -> {
                        info shouldBe "unknown"
                        error shouldNotBe null
                    }
                    else -> org.junit.jupiter.api.fail("Something unexpected happened! ($event)", error)
                }
            }
        }
        PocketCommand.register(mockedPlugin, command)
        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("unknown")) shouldBe true

        preCalled shouldBe true
    }

    "an exception in onPreReload does not lead to onInvalid being called when reloading s single config file" {
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.PRE_RELOAD -> throw RuntimeException("imagine a severe error here")
                    else -> org.junit.jupiter.api.fail("Something unexpected happened! ($event)", error)
                }
            }
        }
        PocketCommand.register(mockedPlugin, command)

        val thrown = shouldThrow<RuntimeException> {
            command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config"))
        }
        thrown.message shouldBe "imagine a severe error here"
    }

    "an exception in onPreReload does not lead to onInvalid being called when reloading all config files" {
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.PRE_RELOAD -> throw RuntimeException("imagine a severe error here")
                    else -> org.junit.jupiter.api.fail("Something unexpected happened!", error)
                }
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

    "reloading all configs must not result in an INVALID event" {
        val configMock = mockk<PocketConfig>()
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, configMock) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.INVALID -> io.kotest.assertions.fail("Event.INVALID must not occur!")
                    Event.ERROR -> {
                        info shouldBe null
                        error should beInstanceOf<IOException>()
                    }
                    else -> {} // ignore
                }
            }
        }
        PocketCommand.register(mockedPlugin, command)

        every { configMock.reloadAll() } throws IOException("imagine an I/O error here")

        command.onCommand(sender, pluginCommand, pluginCommand.label, emptyArray())
    }

    "an unexpected exception while reloading all configs leads to Event.AFTER_RELOAD not being called" {

    }

    "an unexpected exception causes the operation to be aborted" {
        var preCalls = 0
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override fun onEvent(event: Event, sender: CommandSender, info: String?, error: Throwable?) {
                super.onEvent(event, sender, info, error)
                when(event) {
                    Event.PRE_RELOAD -> preCalls++
                    Event.SINGLE -> throw IOException("imagine the file could not be read")
                    Event.ERROR -> {
                        info shouldBe null
                        error should beInstanceOf<IOException>()
                    }
                    else -> org.junit.jupiter.api.fail("Something unexpected happened!", error)
                }
            }
        }
        PocketCommand.register(mockedPlugin, command)

        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config", "anotherConfig")) shouldBe true
        preCalls shouldBe 1
    }

    "invalid usage returns true" {
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override val canReloadSingle: Boolean = false
        }
        PocketCommand.register(mockedPlugin, command)

        command.onCommand(sender, pluginCommand, pluginCommand.label, arrayOf("config", "anotherConfig")) shouldBe false
        command.onCommand(sender, pluginCommand, pluginCommand.label, emptyArray()) shouldBe true
    }

    "default getUsage implementation respects the canReloadSingle setting" {
        var localCanReloadSingle = true
        val command = object : ReloadConfigCommand<CustomMockPlugin>(mockedPlugin, pocketConfig) {
            override val canReloadSingle: Boolean
                get() = localCanReloadSingle
        }
        PocketCommand.register(mockedPlugin, command)

        command.getUsage() shouldBe "/reload [config]"

        localCanReloadSingle = false

        command.getUsage() shouldBe "/reload"
    }
})