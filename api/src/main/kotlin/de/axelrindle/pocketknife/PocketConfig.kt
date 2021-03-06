package de.axelrindle.pocketknife

import org.apache.commons.io.IOUtils
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * A helper class for easily managing and accessing config files.
 * Eliminates the need for manually handling loading and saving.
 *
 * @see YamlConfiguration
 * @since 1.0.0
 */
class PocketConfig(
        private val plugin: JavaPlugin
) {

    private val configFiles: HashMap<String, String> = HashMap()
    private val configInstances: HashMap<String, YamlConfiguration> = HashMap()

    private fun createDefaultFile(name: String, file: File, defaults: InputStream?) {
        if (file.exists()) return

        plugin.logger.info("Creating new config file '$name' at ${file.absolutePath}")

        if (plugin.dataFolder.exists().not() && plugin.dataFolder.mkdirs().not()) {
            plugin.logger.severe("Failed to create the directory '${plugin.dataFolder.absolutePath}'!")
            return
        }

        // check whether to create parent directories
        if (plugin.dataFolder.absolutePath != file.parentFile?.absolutePath) {
            file.parentFile.mkdirs()
        }

        if (file.createNewFile().not()) {
            plugin.logger.severe("Failed to create a config file for '$name'!")
            return
        }

        if (defaults != null) {
            val string = IOUtils.toString(defaults, StandardCharsets.UTF_8)
            FileWriter(file).use { IOUtils.write(string, it) }
        }
    }

    @JvmSynthetic
    internal fun register(directory: File, name: String, defaults: InputStream? = null) {
        // we don't want duplicated or overwritten data
        if (configFiles.containsKey(name))
            throw IllegalArgumentException("A config named '$name' is already registered!")

        // store pathname
        val file = File(directory, "$name.yml")
        configFiles[name] = file.absolutePath

        // create a new config instance
        val config = YamlConfiguration()
        config.options().apply {
            indent(2)
            copyDefaults(true)
        }

        // create default file if given
        createDefaultFile(name, file, defaults)

        // apply defaults to the config instance
        if (defaults != null) {
            InputStreamReader(defaults).use { config.addDefaults(YamlConfiguration.loadConfiguration(it)) }
            defaults.close()
        }

        // load and store config instance
        try {
            config.load(file)
            configInstances[name] = config
        } catch (e: IOException) {
            plugin.logger.severe("Failed to load the configuration from file '${file.absolutePath}'!")
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            plugin.logger.severe("The configuration file '${file.absolutePath}' is invalid!")
        }
    }

    /**
     * Registers a new config file.
     *
     * @param name The name of the file.
     * @param defaults An optional [InputStream] from which default config entries can be read.
     *
     * @throws IllegalArgumentException If a config is already registered.
     * @throws IOException If an I/O error occurs.
     */
    fun register(name: String, defaults: InputStream? = null) {
        register(plugin.dataFolder, name, defaults)
    }

    /**
     * Retrieves a list of all registered config files.
     *
     * @return An [ArrayList] holding a list of registered config file names.
     * @since 2.2.0
     */
    fun list(): ArrayList<String> {
        return ArrayList(configFiles.keys)
    }

    /**
     * @return A [YamlConfiguration] instance for the given config, or `null`.
     */
    fun access(name: String): YamlConfiguration? {
        return configInstances[name]
    }

    /**
     * Reloads a configuration from the disk.
     *
     * @param name The config to reload.
     *
     * @throws IllegalArgumentException If the given config was not found.
     */
    fun reload(name: String) {
        // make sure the given config exists
        if (!configInstances.containsKey(name))
            throw IllegalArgumentException("No config '$name' is registered!")

        // load from the disk and overwrite the mapped value
        val config = YamlConfiguration.loadConfiguration(File(configFiles[name]))
        val defaults = configInstances[name]?.defaults // preserve initially loaded defaults
        if (defaults != null) config.setDefaults(defaults)
        configInstances[name] = config
    }

    /**
     * Reloads all config files from the disk.
     *
     * @see reload
     * @since 1.3.0
     */
    fun reloadAll() {
        configFiles.keys.forEach(this::reload)
    }

    /**
     * Edit a configuration. Saving is done automatically.
     *
     * @param name The config to edit.
     *
     * @throws IllegalArgumentException If the given config was not found.
     * @throws IOException If an I/O error occurs while saving the changes to disk.
     */
    fun edit(name: String, handler: (config: YamlConfiguration) -> Unit) {
        // make sure the given config exists
        if (!configInstances.containsKey(name))
            throw IllegalArgumentException("No config '$name' is registered!")

        // apply changes
        val config = configInstances[name]!!
        handler(config)

        // save to disk
        val file = configFiles[name]!!
        config.save(file)
    }
}