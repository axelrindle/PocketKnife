import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Entity
import org.bukkit.entity.SpawnCategory
import org.bukkit.profile.PlayerProfile
import java.util.*

class CustomPlayerMock(
    server: ServerMock?,
    name: String?,
    uuid: UUID?
) : PlayerMock(server, name, uuid) {

    private var _locale: String = "en"

    override fun getLocale(): String {
        return _locale
    }

    fun setLocale(locale: String) {
        _locale = locale
    }

    override fun getSpawnCategory(): SpawnCategory {
        return SpawnCategory.MISC
    }

    override fun getPlayerProfile(): PlayerProfile {
        TODO("Not yet implemented")
    }

    override fun playSound(entity: Entity, sound: Sound, volume: Float, pitch: Float) {
        TODO("Not yet implemented")
    }

    override fun playSound(entity: Entity, sound: Sound, category: SoundCategory, volume: Float, pitch: Float) {
        TODO("Not yet implemented")
    }

    override fun getPreviousGameMode(): GameMode? {
        TODO("Not yet implemented")
    }

}