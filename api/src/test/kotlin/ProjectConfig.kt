import io.kotest.core.config.AbstractProjectConfig

object ProjectConfig : AbstractProjectConfig() {

    override val parallelism: Int
        get() = Runtime.getRuntime().availableProcessors()
}