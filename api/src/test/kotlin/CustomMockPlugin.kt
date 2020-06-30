import org.bukkit.command.PluginCommand
import org.bukkit.command.PluginCommandUtils
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.jetbrains.annotations.NotNull
import java.io.File
import java.nio.file.Path

/**
 * Custom mocked plugin.
 */
@Suppress("unused")
class CustomMockPlugin : JavaPlugin {

    constructor() : super()
    constructor(
            loader: JavaPluginLoader,
            description: PluginDescriptionFile,
            dataFolder: File,
            file: File?
    ) : super(loader, description, dataFolder, File(System.getProperty("user.dir", "api/src/test/kotlin/CustomMockPlugin.kt")))

    override fun getCommand(name: String): PluginCommand? {
        return PluginCommandUtils.createPluginCommand(name, this)
    }
}