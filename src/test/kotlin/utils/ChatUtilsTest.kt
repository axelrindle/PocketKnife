package utils

import de.axelrindle.pocketknife.util.ChatUtils
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class ChatUtilsTest : ShouldSpec({

    "formatColors" {
        should("replace & with §") {
            ChatUtils.formatColors("&cHello World") shouldBe "§cHello World"
        }
    }
})