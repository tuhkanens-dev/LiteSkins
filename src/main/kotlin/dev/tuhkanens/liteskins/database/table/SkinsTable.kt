package dev.tuhkanens.liteskins.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object SkinsTable : IntIdTable("skins") {
    val playerName = varchar("player_name", 16).uniqueIndex()
    val skinValue = text("skin_value")
    val skinSignature = text("skin_signature")
    val lastUpdated = long("last_updated")
}