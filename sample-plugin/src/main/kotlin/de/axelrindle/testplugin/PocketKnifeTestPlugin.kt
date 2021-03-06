package de.axelrindle.testplugin

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import de.axelrindle.testplugin.command.PocketKnifeCommand
import org.bukkit.plugin.java.JavaPlugin

class PocketKnifeTestPlugin : JavaPlugin() {

    internal val config = PocketConfig(this)
    internal val localization = PocketLang(this, config)

    override fun onEnable() {
        logger.info("Startup...")

        // load configs
        config.register("config")
        localization.addLanguages("de", "en")
        localization.init()

        // commands
        PocketCommand.register(this, PocketKnifeCommand(this))

        logger.info("Loaded v${description.version}")
    }

    override fun onDisable() {

    }
}