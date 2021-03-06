import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.File

class PocketConfigTest : ShouldSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.createMockPlugin()
    val pocketConfig = PocketConfig(mockedPlugin)

    context("register") {
        should("create a non-existing file with defaults normally") {
            pocketConfig.register("testConfig",
                    PocketConfigTest::class.java.getResourceAsStream("/testConfig.yml"))

            val file = File(mockedPlugin.dataFolder, "testConfig.yml")
            val contents = file.readText()
            contents shouldBe "Boolean: false\n" +
                    "String: Hello\n" +
                    "Int: 345\n"
        }

        should("fail for an already registered file") {
            val exception = shouldThrow<IllegalArgumentException> {
                pocketConfig.register("testConfig")
            }
            exception.message shouldBe "A config named 'testConfig' is already registered!"
        }
    }

    context("edit") {
        should("fail for an unknown file") {
            val exception = shouldThrow<IllegalArgumentException> {
                pocketConfig.edit("iDoNotExist") {}
            }
            exception.message shouldBe "No config 'iDoNotExist' is registered!"
        }

        should("edit the data in-memory and immediately write the changes to disk") {
            val file = File(mockedPlugin.dataFolder, "testConfig.yml")
            val contents = file.readText()
            pocketConfig.edit("testConfig") {
                it.set("Float", 2.5f)
            }
            val newContents = file.readText()
            contents shouldNotBe newContents
            newContents shouldBe "Boolean: false\n" +
                    "String: Hello\n" +
                    "Int: 345\n" +
                    "Float: 2.5\n"
        }
    }

    context("reload") {
        should("fail for an unknown config file") {
            val exception = shouldThrow<IllegalArgumentException> {
                pocketConfig.reload("iDoNotExist")
            }
            exception.message shouldBe "No config 'iDoNotExist' is registered!"
        }

        should("load changes for a known file") {
            val file = File(mockedPlugin.dataFolder, "testConfig.yml")
            file.appendText("Another: value\n")

            pocketConfig.reloadAll()
            pocketConfig.access("testConfig")?.get("Another") shouldBe "value"

            val contents = file.readText()
            contents shouldBe "Boolean: false\n" +
                    "String: Hello\n" +
                    "Int: 345\n" +
                    "Float: 2.5\n" +
                    "Another: value\n"
        }
    }

})