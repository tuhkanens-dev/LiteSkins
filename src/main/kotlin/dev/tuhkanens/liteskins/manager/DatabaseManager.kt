package dev.tuhkanens.liteskins.manager

import dev.tuhkanens.liteskins.Main
import dev.tuhkanens.liteskins.database.InterfaceDatabase
import dev.tuhkanens.liteskins.database.bases.MySQLDatabase
import dev.tuhkanens.liteskins.database.bases.SQLiteDatabase

object DatabaseManager {

    private val plugin: Main = Main.instance

    lateinit var currentDatabase: InterfaceDatabase
        private set

    fun connect() {

        if (::currentDatabase.isInitialized) {
            plugin.logger.warn("Database is already connected!")
            return
        }

        val config = ConfigManager.get()
        val database = when (val provider = config.node("database", "provider").getString("sqlite")) {
            "sqlite" -> SQLiteDatabase
            "mysql" -> MySQLDatabase
            else -> {
                plugin.logger.warn("Unknown database type: $provider, using SQLite")
                SQLiteDatabase
            }
        }

        database.connect()
        currentDatabase = database

    }

    fun disconnect() {
        if (!::currentDatabase.isInitialized) return

        currentDatabase.disconnect()
        plugin.logger.info("Database connection closed.")
    }

}