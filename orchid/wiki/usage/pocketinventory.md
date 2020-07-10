```kotlin
class ShopCommand(
    private val plugin: TestPlugin
) : PocketCommand() {

    private val inventory: PocketInventory = PocketInventory(plugin, "Shop", 4)

    init {
        // golden apple
        inventory.setItem(11, ItemStack(Material.GOLDEN_APPLE)) { /* buy logic */ }

        // close item
        inventory.setItem(53, ItemStack(Material.DARK_OAK_DOOR_ITEM)) { it.view.close() }
    }

    override fun getName(): String {
        return "shop"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        inventory.open(sender)
        return true
    }

    override fun requirePlayer(): Boolean {
        return true
    }
}
```

It is common to create custom inventories in connection with commands. When calling the command, the custom inventory will be shown to the player.

The fulfill the requirement of a `Player`, the `requirePlayer` method is overridden to return `true`.

This example implementation of the `PocketInventory` creates an inventory with 4 rows and two items contained: a **golden apple** in the middle of the second row and a **dark oak door** at the bottom right corner.

While living inside a `PocketCommand` the inventory is shown every player calling the associated command (`/shop` in this case).
