import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketInventory
import io.kotlintest.shouldThrow
import io.kotlintest.specs.ShouldSpec
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PocketInventoryTest : ShouldSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.createMockPlugin()

    "PocketInventory" {

        "instantiation" {
            should("succeed with size of 4") {
                PocketInventory(mockedPlugin, "MyInventory", 4)
            }
            should("fail with size of 7") {
                shouldThrow<IllegalArgumentException> {
                    PocketInventory(mockedPlugin, "MyInventory", 7)
                }
            }
        }

        val pocketInventory = PocketInventory(mockedPlugin, "MyInventory", 6)

        "setItem" {
            should("fail with negative position") {
                shouldThrow<IllegalArgumentException> {
                    pocketInventory.setItem(-4, ItemStack(Material.APPLE))
                }
            }

            should("succeed with valid position") {
                pocketInventory.setItem(2, ItemStack(Material.CHEST))
            }
        }


    }
})