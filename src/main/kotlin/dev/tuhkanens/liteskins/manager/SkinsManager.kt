package dev.tuhkanens.liteskins.manager

import dev.tuhkanens.liteskins.fetch.SkinFetch

object SkinsManager {

    private val db get() = DatabaseManager.currentDatabase

    fun getSkin(playerName: String) = db.getSkin(playerName)
    fun addSkin(playerName: String, value: String, signature: String) = db.addSkin(playerName, value, signature)
    fun hasSkin(playerName: String) = db.hasSkin(playerName)
    fun updateSkin(playerName: String, value: String, signature: String) = db.updateSkin(playerName, value, signature)
    fun deleteSkin(playerName: String) = db.deleteSkin(playerName)

    fun skinFetch(playerName: String) = SkinFetch.fetch(playerName)

}