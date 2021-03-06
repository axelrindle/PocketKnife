import be.seeseemelk.mockbukkit.MockBukkit
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan

@AutoScan
object TestInit : ProjectListener {

    override suspend fun beforeProject() {
        MockBukkit.mock()
        MockBukkit.getMock().setPlayers(10)
    }

    override suspend fun afterProject() {
        MockBukkit.unmock()
    }

}