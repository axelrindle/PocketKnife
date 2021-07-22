package de.axelrindle.pocketknife

import org.apache.commons.io.FilenameUtils
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Files

/**
 * A helper class which assists in localizing your plugin.
 *
 * @since 1.0.0
 */
class PocketLang(
        private val plugin: JavaPlugin,
        private val pocketConfig: PocketConfig
) {

    companion object {
        /**
         * The name for the config which holds localization settings.
         */
        const val CONFIG_NAME = "localization"
    }

    private val supportedLanguages: ArrayList<String> = ArrayList()

    /**
     * Registers a new supported language.
     *
     * @param language The language to add.
     *
     * @throws IllegalArgumentException If the given language is already registered.
     */
    fun addLanguage(language: String) {
        if (supportedLanguages.contains(language))
            throw IllegalArgumentException("Locale '$language' is already registered!")

        supportedLanguages += language
    }

    /**
     * Register one or more supported languages.
     *
     * @param list The list of languages to register.
     * @see addLanguage
     */
    fun addLanguages(vararg list: String) = list.forEach(this::addLanguage)

    /**
     * @param overrideDefault DEPRECATED! An optional value which overrides the default language, which is
     *                        set to "en" by default.
     */
    @Deprecated(
            message = "The overrideDefault is not used anymore.",
            replaceWith = ReplaceWith(
                    "init()"
            )
    )
    fun init(overrideDefault: String? = null) {
        // only kept for compatibility reasons
        plugin.logger.warning("fun init(overrideDefault: String? = null) is deprecated! Use init() instead.")
        init()
    }

    /**
     * Does config and message registration. Call this AFTER you've added your
     * supported languages.
     *
     * @see PocketConfig
     * @throws IllegalStateException If no languages were registered.
     */
    fun init() {
        if (supportedLanguages.size == 0)
            throw IllegalStateException("No languages were registered!")

        // create the lang directory if it does not exist
        val langDir = File(plugin.dataFolder, "lang").toPath()
        if (! Files.isDirectory(langDir)) {
            Files.createDirectory(langDir)
        }

        // load any additional found lang files
        var loadedAdditional = false
        Files.list(langDir)
            .map { el -> FilenameUtils.getBaseName(el.fileName.toString()) }
            .filter { el -> ! supportedLanguages.contains(el) }
            .forEach { el ->
                supportedLanguages.add(el)
                loadedAdditional = true
            }
        if (loadedAdditional) {
            plugin.logger.info("Loaded additional languages")
        }

        // register own config file
        val pocketKnifeDir = plugin.dataFolder.parentFile.resolve("PocketKnife")
        pocketConfig.register(pocketKnifeDir, CONFIG_NAME, javaClass.getResourceAsStream("/localization.yml"))

        // register appropriate config files
        supportedLanguages.forEach {
            val path = "lang/$it"
            pocketConfig.register(path, plugin.getResource("$path.yml"))
        }
    }

    @JvmSynthetic
    internal fun getDefaultConfig(): YamlConfiguration? {
        val localization = pocketConfig.access(CONFIG_NAME)
        val language = localization?.getString("DefaultLanguage")
        return pocketConfig.access("lang/$language")
    }

    @JvmSynthetic
    internal fun getLocaleConfig(locale: String? = null): YamlConfiguration? {
        val localization = pocketConfig.access(CONFIG_NAME)
        val language = locale ?: localization?.getString("UseLanguage")
        return pocketConfig.access("lang/$language")
    }

    /**
     * Returns the localized string for the given key.
     *
     * @param key The localizing key.
     * @param player When given, the player's locale is used for localization.
     * @param args Any arguments to format the localized string with.
     *
     * @return The localized string, the default localized string, or `null`.
     * @see String.format
     * @since 2.2.0
     */
    fun localize(key: String, player: Player? = null, vararg args: Any?): String? {
        val localeConfig = getLocaleConfig(player?.locale) ?: getLocaleConfig()
        val supposed = localeConfig?.getString(key)
        val default = getDefaultConfig()?.getString(key)

        // nothing was found
        if (supposed == null && default == null) {
            plugin.logger.severe("No default entry was found for localization key '$key'!")
            return null
        }

        // only default
        if (supposed == null && default != null) {
            plugin.logger.warning("Only a default translation was found for localization key '$key'!")
            return default.format(*args)
        }

        // translated
        return supposed?.format(*args)
    }

    /**
     * Old version of [localize]. Use the new one instead.
     */
    @Deprecated(
        "Only kept for compatibility. Use the new function.",
        ReplaceWith("localize(key, null, args)")
    )
    fun localize(key: String, vararg args: Any?) = localize(key, null, args)
}