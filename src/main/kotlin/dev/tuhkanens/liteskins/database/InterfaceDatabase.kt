package dev.tuhkanens.liteskins.database

import dev.tuhkanens.liteskins.database.result.DatabaseResult
import dev.tuhkanens.liteskins.data.PlayerSkin

interface InterfaceDatabase {
    fun connect()
    fun disconnect()
    fun addSkin(playerName: String, value: String, signature: String): DatabaseResult<Nothing>
    fun hasSkin(playerName: String): DatabaseResult<Boolean>
    fun getSkin(playerName: String): DatabaseResult<PlayerSkin>
    fun updateSkin(playerName: String, value: String, signature: String): DatabaseResult<Nothing>
    fun deleteSkin(playerName: String): DatabaseResult<Nothing>
}