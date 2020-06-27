package utils

import de.axelrindle.pocketknife.util.LocationUtils
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.bukkit.Location

class LocationUtilsTest : ShouldSpec({

    val loc = Location(null, 10.0, 73.0, 64.0)

    context("serializeLocation") {
        should("behave as expected") {
            LocationUtils.serializeLocation(loc) shouldBe "null;10.0;73.0;64.0;0.0;0.0"
        }
    }

    context("deserializeLocation") {
        should("behave as expected") {
            LocationUtils.deserializeLocation("null;10.0;73.0;64.0;0.0;0.0") shouldBe loc
        }
    }
})