import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.endWith
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class PocketLangTest : ShouldSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.createMockPlugin()
    val pocketConfig = PocketConfig(mockedPlugin)
    val pocketLang = PocketLang(mockedPlugin, pocketConfig)

    should("PocketLang.getDefaultConfig() should return null before init") {
        pocketLang.getDefaultConfig() shouldBe null
    }

    should("PocketLang.getLocaleConfig() should return null before init") {
        pocketLang.getLocaleConfig() shouldBe null
    }

    should("PocketLang.init() fail without adding languages") {
        val exception = shouldThrow<IllegalStateException> {
            pocketLang.init()
        }
        exception.message shouldBe "No languages were registered!"
    }

    should("PocketLang.init() succeed") {
        pocketLang.addLanguages("en", "de")

        val exception = shouldThrow<IllegalArgumentException> {
            pocketLang.addLanguage("en")
        }
        exception.message should endWith("is already registered!")

        pocketLang.init()
    }

    should("PocketLang.getDefaultConfig() should NOT return null AFTER init") {
        pocketLang.getDefaultConfig() shouldNotBe null
    }

    should("PocketLang.getLocaleConfig() should NOT return null AFTER init") {
        pocketLang.getLocaleConfig() shouldNotBe null
    }
})