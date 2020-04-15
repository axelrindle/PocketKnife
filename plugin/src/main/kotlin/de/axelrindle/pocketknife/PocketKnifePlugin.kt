package de.axelrindle.pocketknife

import org.bukkit.plugin.java.JavaPlugin

/**
 * This class serves no direct purpose, but is required so Bukkit loads the plugin's classpath.
 */
@Suppress("unused")
class PocketKnifePlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("PocketKnife v${description.version} enabled.")
    }

    override fun onDisable() {
        logger.info("PocketKnife v${description.version} disabled.")
    }
}