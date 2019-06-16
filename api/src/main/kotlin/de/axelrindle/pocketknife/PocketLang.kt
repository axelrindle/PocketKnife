package de.axelrindle.pocketknife

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

/**
 * A helper class which assists in localizing your plugin.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class PocketLang(
        private val plugin: JavaPlugin,
        private val pocketConfig: PocketConfig
) {

    private val configName = "localization"
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
     * Does config and message registration. Call this AFTER you've added your
     * supported languages.
     *
     * @param overrideDefault An optional value which overrides the default language, which is
     *                        set to "en" by default.
     *
     * @see PocketConfig
     * @throws IllegalStateException If no languages were registered.
     */
    fun init(overrideDefault: String? = null) {
        if (supportedLanguages.size == 0)
            throw IllegalStateException("No languages were registered!")

        // register own config file
        pocketConfig.register(configName, javaClass.getResourceAsStream("/localization.yml"))
        if (overrideDefault != null)
            pocketConfig.edit(configName) {
                it.set("UseLanguage", overrideDefault)
                it.set("DefaultLanguage", overrideDefault)
            }

        // register appropriate config files
        supportedLanguages.forEach {
            val path = "lang/$it"
            pocketConfig.register(path, plugin.getResource("$path.yml")!!)
        }
    }

    internal fun getDefaultConfig(): YamlConfiguration? {
        val localization = pocketConfig.access(configName)
        val language = localization?.getString("DefaultLanguage")
        return pocketConfig.access("lang/$language")
    }

    internal fun getLocaleConfig(): YamlConfiguration? {
        val localization = pocketConfig.access(configName)
        val language = localization?.getString("UseLanguage")
        return pocketConfig.access("lang/$language")
    }

    /**
     * Returns the localized string for the given key.
     *
     * @param key The localizing key.
     * @param args Any arguments to format the localized string with.
     *
     * @return The localized string, the default localized string, or `null`.
     * @see String.format
     */
    fun localize(key: String, vararg args: Any?): String? {
        val supposed = getLocaleConfig()?.getString(key)
        val default = getDefaultConfig()?.getString(key)

        // nothing was found
        if (supposed == null && default == null) {
            plugin.logger.warning("No default entry was found for localization key '$key'!")
            return null
        }

        // only default
        if (supposed == null && default != null) {
            plugin.logger.warning("Only a default translation was found for localization key '$key'!")
            return default.format(args)
        }

        // translated
        return supposed?.format(args)
    }
}