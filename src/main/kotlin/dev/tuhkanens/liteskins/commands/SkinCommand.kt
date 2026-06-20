package dev.tuhkanens.liteskins.commands

import com.velocitypowered.api.command.RawCommand
import com.velocitypowered.api.proxy.Player
import dev.tuhkanens.liteskins.Main
import dev.tuhkanens.liteskins.database.result.DatabaseResult
import dev.tuhkanens.liteskins.fetch.result.SkinFetchResult
import dev.tuhkanens.liteskins.manager.ConfigManager
import dev.tuhkanens.liteskins.manager.MessagesManager
import dev.tuhkanens.liteskins.manager.SkinsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

class SkinCommand : RawCommand {

    override fun execute(invocation: RawCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments().trim().split(" ").filter { it.isNotBlank() }

        val config = ConfigManager.get().node("permissions")

        if (source !is Player) {
            source.sendMessage(MessagesManager.getComponent("commands.only_player"))
            return
        }

        if (args.isEmpty()) {
            source.sendMessage(MessagesManager.getComponent("commands.skin.usage"))
            return
        }

        when (args[0].lowercase()) {
            "set" -> {
                if (!source.hasPermission(config.node("set").getString("liteskins.set"))) {
                    source.sendMessage(MessagesManager.getComponent("commands.invalid_permission"))
                    return
                }
                handleSet(source, args)
            }
            "clear" -> {
                if (!source.hasPermission(config.node("clear").getString("liteskins.clear"))) {
                    source.sendMessage(MessagesManager.getComponent("commands.invalid_permission"))
                    return
                }
                handleClear(source)
            }
            "help" -> handleHelp(source)
            else -> source.sendMessage(MessagesManager.getComponent("commands.skin.usage"))
        }
    }

    private fun handleHelp(player: Player) {
        MessagesManager.getComponentList("commands.skin.help.success")
            .forEach { player.sendMessage(it) }
    }

    private fun handleSet(player: Player, args: List<String>) {
        if (args.size < 2) {
            player.sendMessage(MessagesManager.getComponent("commands.skin.set.usage"))
            return
        }

        val targetName = args[1]
        player.sendMessage(
            MessagesManager.getComponent(
                "commands.skin.set.receive",
                Placeholder.unparsed("target", targetName)
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            when (val fetchResult = SkinsManager.skinFetch(targetName)) {
                is SkinFetchResult.GetSuccess -> {
                    val skin = fetchResult.data
                    val playerName = player.username

                    val saveResult = when (SkinsManager.hasSkin(playerName)) {
                        is DatabaseResult.Success ->
                            SkinsManager.updateSkin(playerName, skin.value, skin.signature)
                        else ->
                            SkinsManager.addSkin(playerName, skin.value, skin.signature)
                    }

                    when (saveResult) {
                        is DatabaseResult.Success, is DatabaseResult.Already -> {
                            player.sendMessage(MessagesManager.getComponent("commands.skin.set.success"))
                        }
                        is DatabaseResult.Failure -> {
                            player.sendMessage(
                                MessagesManager.getComponent(
                                    "commands.skin.set.error_save",
                                    Placeholder.unparsed("error", saveResult.error)
                                )
                            )
                        }
                        else -> {
                            player.sendMessage(MessagesManager.getComponent("commands.skin.set.failed_save"))
                        }
                    }
                }
                is SkinFetchResult.Failure -> {
                    player.sendMessage(
                        MessagesManager.getComponent(
                            "commands.skin.set.target_not_found",
                            Placeholder.unparsed("target", targetName)
                        )
                    )
                }
            }
        }
    }

    private fun handleClear(player: Player) {
        val playerName = player.username

        when (val result = SkinsManager.deleteSkin(playerName)) {
            is DatabaseResult.Success -> {
                player.sendMessage(MessagesManager.getComponent("commands.skin.clear.success"))
            }
            is DatabaseResult.NotFound -> {
                player.sendMessage(MessagesManager.getComponent("commands.skin.clear.skin_not_found"))
            }
            is DatabaseResult.Failure -> {
                player.sendMessage(
                    MessagesManager.getComponent(
                        "commands.skin.clear.error_clear",
                        Placeholder.unparsed("error", result.error)
                    )
                )
            }
            else -> {
                player.sendMessage(MessagesManager.getComponent("commands.skin.clear.failed_clear"))
            }
        }
    }

    override fun suggest(invocation: RawCommand.Invocation): List<String> {
        val raw = invocation.arguments()
        val args = raw.split(" ")

        return when (args.size) {
            1 -> listOf("set", "clear", "help")
                .filter { it.startsWith(args[0], ignoreCase = true) }

            2 -> when (args[0].lowercase()) {
                "set" -> {
                    val partial = args[1]
                    Main.instance.proxy.allPlayers
                        .map { it.username }
                        .filter { it.startsWith(partial, ignoreCase = true) }
                }
                else -> emptyList()
            }

            else -> emptyList()
        }
    }



}