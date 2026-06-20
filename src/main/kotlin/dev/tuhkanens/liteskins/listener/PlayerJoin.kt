package dev.tuhkanens.liteskins.listener

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.GameProfileRequestEvent
import com.velocitypowered.api.util.GameProfile
import dev.tuhkanens.liteskins.Main
import dev.tuhkanens.liteskins.database.result.DatabaseResult
import dev.tuhkanens.liteskins.fetch.result.SkinFetchResult
import dev.tuhkanens.liteskins.manager.DefaultSkinsManager
import dev.tuhkanens.liteskins.manager.SkinsManager

class PlayerJoin {

    private val plugin: Main = Main.instance

    @Subscribe(order = PostOrder.EARLY)
    fun onPlayerJoin(event: GameProfileRequestEvent) {

        val playerName = event.username

        val skin = when (val result = SkinsManager.getSkin(playerName)) {
            is DatabaseResult.GetSuccess -> result.data
            is DatabaseResult.NotFound -> {
                when (val result = SkinsManager.skinFetch(playerName)) {
                    is SkinFetchResult.GetSuccess -> {
                        val skin = result.data
                        SkinsManager.addSkin(playerName, skin.value, skin.signature)
                        skin
                    }
                    else -> DefaultSkinsManager.randomDefaultSkin() ?: run {
                        plugin.logger.warn("Skin not found!")
                        return
                    }
                }
            }
            else -> return
        }

        val originalProfile = event.gameProfile

        val newProperties = originalProfile.properties
            .filter { it.name != "textures" }
            .toMutableList()
        newProperties.add(GameProfile.Property("textures", skin.value, skin.signature))

        val newProfile = GameProfile(
            originalProfile.id,
            originalProfile.name,
            newProperties
        )

        event.gameProfile = newProfile

    }

}