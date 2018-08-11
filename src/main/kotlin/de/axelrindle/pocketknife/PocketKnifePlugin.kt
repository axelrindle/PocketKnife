package de.axelrindle.pocketknife

import org.bukkit.plugin.java.JavaPlugin

class PocketKnifePlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("PocketKnife v${description.version} enabled.")
    }

    override fun onDisable() {
        logger.info("PocketKnife v${description.version} disabled.")
    }
}