import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.endWith
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class PocketLangTest : ShouldSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.createMockPlugin()
    val pocketConfig = PocketConfig(mockedPlugin)
    val pocketLang = PocketLang(mockedPlugin, pocketConfig)

    // create additional file
    val russianConfig = YamlConfiguration()
    russianConfig.set("message3", "привет %s")
    russianConfig.save(File(mockedPlugin.dataFolder, "lang/ru.yml"))

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

            pocketLang.init()
        }

        // lets pretend we edit the default language
        pocketConfig.edit(PocketLang.CONFIG_NAME) { conf ->
            conf.set("DefaultLanguage", "de")
            conf.set("UseLanguage", "de")
        }

        should("correctly override default language") {
            pocketConfig.access("localization")!!.apply {
                get("UseLanguage") shouldBe "de"
                get("DefaultLanguage") shouldBe "de"
            }
        }

        should("supported additional languages") {
            val field = PocketLang::class.memberProperties.find { el -> el.name == "supportedLanguages" }
            field shouldNotBe null
            field?.isAccessible = true
            val list = field?.get(pocketLang) as ArrayList<String>
            list shouldContain "ru"
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