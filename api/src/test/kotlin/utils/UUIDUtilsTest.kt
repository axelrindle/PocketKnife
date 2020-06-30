package utils

import de.axelrindle.pocketknife.util.UUIDUtils
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class UUIDUtilsTest : ShouldSpec({

    val theUuidString = UUIDUtils.trimmedToFull("2dcd2efee0ef40e981c5345de1b8ff65")
    theUuidString shouldBe "2dcd2efe-e0ef-40e9-81c5-345de1b8ff65"
    UUIDUtils.isValid(theUuidString) shouldBe true

    UUIDUtils.isValid("this is not an uuid") shouldBe false

})