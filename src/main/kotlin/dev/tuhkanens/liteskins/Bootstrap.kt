package dev.tuhkanens.liteskins

import com.velocitypowered.api.proxy.ProxyServer
import dev.tuhkanens.liteskins.commands.SkinCommand
import dev.tuhkanens.liteskins.listener.PlayerJoin
import dev.tuhkanens.liteskins.manager.ConfigManager
import dev.tuhkanens.liteskins.manager.DatabaseManager
import dev.tuhkanens.liteskins.manager.DefaultSkinsManager
import dev.tuhkanens.liteskins.manager.MessagesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Bootstrap {

    private val plugin: Main = Main.instance
    private val proxy: ProxyServer = plugin.proxy

    fun initialize() {
        ConfigManager.init()
        MessagesManager.init()

        DatabaseManager.connect()

        CoroutineScope(Dispatchers.Default).launch {
            DefaultSkinsManager.loadSkins()
        }

        this.registerListeners()
        this.registerCommands()
    }

    private fun registerListeners() {
        proxy.eventManager.register(plugin, PlayerJoin())
    }

    private fun registerCommands() {
        proxy.commandManager.register(
            proxy.commandManager.metaBuilder("skin").build(),
            SkinCommand()
        )
    }

    fun shutdown() {
        DatabaseManager.disconnect()
    }

}