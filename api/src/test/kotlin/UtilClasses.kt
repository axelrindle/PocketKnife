import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
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

}