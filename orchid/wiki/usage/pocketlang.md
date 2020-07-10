```kotlin
class MyPlugin : JavaPlugin(), Listener {

    internal val config = PocketConfig(this)
    internal val localization = PocketLang(this, config)

    override fun onEnable() {
        logger.info("Startup...")

        // load configs
        config.register("config", javaClass.getResourceAsStream("/config.yml"))
        localization.addLanguages("de", "en")
        localization.init("de")

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

For an example implementation of using the `PocketLang` class, let's extend our `PocketConfig` example. A `PocketLang` instance is created with a reference to the plugin **and our previously created config instance**.

For every language that is added an own configuration file for it is registered at `lang/<language>`. A default version in the classpath is required in this case.

Initialization can be run with an optional parameter to override the default language value `en` (for English).
