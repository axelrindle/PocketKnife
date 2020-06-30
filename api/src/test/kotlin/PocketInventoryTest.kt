import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.pocketknife.PocketInventory
import de.axelrindle.pocketknife.util.InventoryUtils
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class PocketInventoryTest : ShouldSpec({

    // create a fake bukkit environment
    val mockedPlugin = MockBukkit.createMockPlugin()
    val player = MockBukkit.getMock().getPlayer(0)

    context("instantiation") {
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
    val pane = InventoryUtils.makeStack(Material.BLUE_STAINED_GLASS_PANE, "-")
    for (x in 1..9) {
        for (y in 1..6) {
            if (x == 1 || x == 9 || y == 1 || y == 6) {
                val index = InventoryUtils.getIndex(x, y)
                pocketInventory.setItem(index, pane)
            }
        }
    }

    context("setItem") {
        should("fail with negative position") {
            shouldThrow<IllegalArgumentException> {
                pocketInventory.setItem(-4, ItemStack(Material.APPLE))
            }
        }

        should("succeed with valid position") {
            val stack = ItemStack(Material.ENDER_EYE)
            val pos = InventoryUtils.getIndex(5, 3)
            pocketInventory.setItem(pos, stack)
            pocketInventory.getItem(pos) shouldBeSameInstanceAs stack
        }
    }

    context("open") {
        should("show the inventory and correctly set all items") {
            pocketInventory.open(player) {
                it.getItem(InventoryUtils.getIndex(5, 3)) shouldNotBe null
                it.getItem(InventoryUtils.getIndex(5, 3))?.type shouldBe Material.ENDER_EYE
                it.getItem(0)?.type shouldBe Material.BLUE_STAINED_GLASS_PANE
            }
            player.openInventory.title shouldBe pocketInventory.name
            player.openInventory.type shouldBe InventoryType.CHEST
        }
    }

    context("removeItem") {
        should("remove an item from the inventory") {
            pocketInventory.removeItem(0)
            pocketInventory.getItem(0) shouldBe null
        }
    }
})