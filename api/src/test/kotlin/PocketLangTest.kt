import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.endWith

class PocketLangTest : ShouldSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.createMockPlugin()
    val pocketConfig = PocketConfig(mockedPlugin)
    val pocketLang = PocketLang(mockedPlugin, pocketConfig)

    context("config getters") {
        should("return null before init") {
            pocketLang.getDefaultConfig() shouldBe null
            pocketLang.getLocaleConfig() shouldBe null
        }
    }

    context("init") {
        should("fail without adding languages") {
            val exception = shouldThrow<IllegalStateException> {
                pocketLang.init()
            }
            exception.message shouldBe "No languages were registered!"
        }

        should("succeed with languages added") {
            pocketLang.addLanguages("en", "de")

            val exception = shouldThrow<IllegalArgumentException> {
                pocketLang.addLanguage("en")
            }
            exception.message should endWith("is already registered!")

            pocketLang.init("de")
        }

        should("correctly override default language") {
            pocketConfig.access("localization")!!.apply {
                get("UseLanguage") shouldBe "de"
                get("DefaultLanguage") shouldBe "de"
            }
        }
    }

    context("localize") {
        should("return null when no matching key is found") {
            pocketLang.localize("unknownMessage") shouldBe null
        }

        should("return the localized string") {
            pocketLang.localize("message3", "Axel") shouldBe "Hallo Axel"
        }
    }
})