package utils

import de.axelrindle.pocketknife.util.ChatUtils
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ChatUtilsTest : ShouldSpec({

    context("formatColors") {
        should("replace & with §") {
            ChatUtils.formatColors("&cHello World") shouldBe "§cHello World"
        }
    }
})