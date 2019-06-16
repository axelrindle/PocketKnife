package utils

import de.axelrindle.pocketknife.util.LocationUtils
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.bukkit.Location

class LocationUtilsTest : ShouldSpec({

    val loc = Location(null, 10.0, 73.0, 64.0)

    "serializeLocation" {
        should("behave as expected") {
            LocationUtils.serializeLocation(loc) shouldBe "null;10.0;73.0;64.0;0.0;0.0"
        }
    }

    "deserializeLocation" {
        should("behave as expected") {
            LocationUtils.deserializeLocation("null;10.0;73.0;64.0;0.0;0.0") shouldBe loc
        }
    }
})