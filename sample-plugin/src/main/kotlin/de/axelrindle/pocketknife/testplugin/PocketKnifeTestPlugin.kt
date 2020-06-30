package de.axelrindle.pocketknife.testplugin

import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import de.axelrindle.pocketknife.testplugin.command.PocketKnifeCommand
import org.bukkit.plugin.java.JavaPlugin

class PocketKnifeTestPlugin : JavaPlugin() {

    internal val config = PocketConfig(this)
    internal val localization = PocketLang(this, config)

    override fun onEnable() {
        logger.info("Startup...")

        // load configs
        config.register("config")
        localization.addLanguages("de", "en")
        localization.init("de")

        // commands
        PocketCommand.register(this, PocketKnifeCommand(this))
    }

    override fun onDisable() {

    }
}