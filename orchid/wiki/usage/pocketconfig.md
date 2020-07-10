```kotlin
class MyPlugin : JavaPlugin(), Listener {

    internal val config = PocketConfig(this)

    override fun onEnable() {
        logger.info("Startup...")

        // load configs
        config.register("config", javaClass.getResourceAsStream("/config.yml"))

        // register events
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        config.edit("config") {
            val uuid = event.player.uniqueId.toString()
            if (it.isConfigurationSection(uuid).not()) {
                it.createSection(uuid)
            }
        }
    }
}
```

An example implementation on using the `PocketConfig`. All you need is an instance of the class and then registering your configuration files.

In this example, a default configuration is provided from the classpath. This can be useful to ensure required values are available when removed from the config file on the filesystem.

Speaking in theoretical terms, you may register as many config files as you want.
