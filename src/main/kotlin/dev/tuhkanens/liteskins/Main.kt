package dev.tuhkanens.liteskins

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.tuhkanens.liteskins.libs.LibraryLoader
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "liteskins",
    name = "liteskins",
    version = "1.0.0",
    description = "Lite Skins for Velocity",
    authors = ["TuhkanenS"]
)
class Main @Inject constructor(
    val proxy: ProxyServer,
    val logger: Logger,
    @DataDirectory val directory: Path
) {

    companion object {
        lateinit var instance: Main
            private set

        val miniMessage: MiniMessage = MiniMessage.miniMessage()
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        instance = this

        LibraryLoader().loadLibraries()
        Bootstrap().initialize()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        Bootstrap().shutdown()
    }
}