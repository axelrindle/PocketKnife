package utils

import de.axelrindle.pocketknife.util.UUIDUtils
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import java.util.*
import java.util.function.Consumer

class UUIDUtilsTest : ShouldSpec({

    val theUuidString = UUIDUtils.trimmedToFull("2dcd2efee0ef40e981c5345de1b8ff65")
    theUuidString shouldBe "2dcd2efe-e0ef-40e9-81c5-345de1b8ff65"
    val theUuid = UUID.fromString(theUuidString)

    should("lookup the valid UUID (\"$theUuidString\")") {
        UUIDUtils.lookup("lalo5", Consumer {
            it shouldBe theUuid
        })
    }

    should("synchronously lookup the valid UUID (\"$theUuidString\")") {
        UUIDUtils.lookupSync("lalo5") shouldBe theUuid
    }
})