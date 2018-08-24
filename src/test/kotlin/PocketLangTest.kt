import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.ShouldSpec

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

    should("PocketLang.init() succeed") {
        pocketLang.addLanguages("en", "de")
        pocketLang.init()
    }

    should("PocketLang.getDefaultConfig() should NOT return null AFTER init") {
        pocketLang.getDefaultConfig() shouldNotBe null
    }

    should("PocketLang.getLocaleConfig() should NOT return null AFTER init") {
        pocketLang.getLocaleConfig() shouldNotBe null
    }
})