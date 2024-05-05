package archyonix.ewars.game.bars

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import java.time.Duration

abstract class GameBossBar(
    private val updateTime: Duration
) {
    private val bossBar = BossBar.bossBar(Component.empty(), 0f, Color.WHITE, BossBar.Overlay.PROGRESS)

    init {
        render()
    }

    abstract fun onRender(): Component
    private fun render() {
        try {
            bossBar.name(onRender())
        } catch (_: Exception) {

        }
        MinecraftServer
            .getSchedulerManager()
            .buildTask(this::render)
            .delay(updateTime)
            .schedule()
    }

    fun addViewer(viewer: Player): GameBossBar {
        viewer.showBossBar(bossBar)
        return this
    }

    fun removeViewer(viewer: Player): GameBossBar {
        viewer.hideBossBar(bossBar)
        return this
    }
}