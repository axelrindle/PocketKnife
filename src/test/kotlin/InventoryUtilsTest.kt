import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.util.InventoryUtils
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.bukkit.Material

class InventoryUtilsTest : ShouldSpec({

    // create a fake environment
    MockBukkit.mock()

    "InventoryUtils.getIndex" {

        should("return 0 for column 1 and row 1") {
            InventoryUtils.getIndex(1, 1) shouldBe 0
        }

        should("return 10 for column 2 and row 2") {
            InventoryUtils.getIndex(2, 2) shouldBe 10
        }

        should("return 20 for column 3 and row 3") {
            InventoryUtils.getIndex(3, 3) shouldBe 20
        }

        should("return 30 for column 4 and row 4") {
            InventoryUtils.getIndex(4, 4) shouldBe 30
        }

        should("return 40 for column 5 and row 5") {
            InventoryUtils.getIndex(5, 5) shouldBe 40
        }

        should("return 50 for column 6 and row 6") {
            InventoryUtils.getIndex(6, 6) shouldBe 50
        }

        should("return 53 for column 9 and row 6") {
            InventoryUtils.getIndex(9, 6) shouldBe 53
        }

        should("return 32 for column 6 and row 4") {
            InventoryUtils.getIndex(6, 4) shouldBe 32
        }

    }

    "InventoryUtils.makeStack" {
        should("properly create an ItemStack") {
            val stack = InventoryUtils.makeStack(Material.DIAMOND, "&b&lDiamond",
                    "", "&5&Shining bright...")

            stack.type shouldBe Material.DIAMOND
            stack.amount shouldBe 1
            stack.itemMeta!!.displayName shouldBe "&b&lDiamond"
            stack.itemMeta!!.lore!!.size shouldBe 2
        }
    }
})