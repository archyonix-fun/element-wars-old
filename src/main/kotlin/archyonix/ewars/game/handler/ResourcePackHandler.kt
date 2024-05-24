package archyonix.ewars.game.handler

import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.resource.ResourcePackStatus
import net.minestom.server.event.PublicEventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerResourcePackStatusEvent
import java.net.URI
import java.util.*

class ResourcePackHandler : PublicEventNode("rp-handler") {
    private val request = ResourcePackRequest.addingRequest(
        ResourcePackInfo.resourcePackInfo(
            UUID.randomUUID(),
            URI("https://github.com/archyonix-fun/resources/raw/main/ElementWars/ElementWars.zip"),
            "9486af36d9d48d0cb5a9d069e0f8d127"
        )
    )

    init {
        addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            event.player.sendResourcePacks(request)
        }

        addListener(PlayerResourcePackStatusEvent::class.java) { event ->
            if(event.status == ResourcePackStatus.DECLINED)
                event.player.kick("Установите пакет ресурсов!")
        }
    }
}