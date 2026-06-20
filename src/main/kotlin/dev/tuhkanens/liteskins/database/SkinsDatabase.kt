package dev.tuhkanens.liteskins.database

import dev.tuhkanens.liteskins.data.PlayerSkin
import dev.tuhkanens.liteskins.database.result.DatabaseResult
import dev.tuhkanens.liteskins.database.table.SkinsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

abstract class SkinsDatabase : InterfaceDatabase {

    lateinit var db: Database

    protected abstract fun createConnection(): Database
    protected open fun closeConnection() {}

    override fun connect() {
        db = createConnection()
        transaction(db) {
            SchemaUtils.create(SkinsTable)
        }
    }

    override fun disconnect() {
        if (!::db.isInitialized) return
        closeConnection()
    }

    override fun addSkin(playerName: String, value: String, signature: String): DatabaseResult<Nothing> {
        return try {
            transaction(db) {
                val exists = existsSkin(playerName)
                if (!exists) {
                    SkinsTable.insert {
                        it[SkinsTable.playerName] = playerName
                        it[skinValue] = value
                        it[skinSignature] = signature
                        it[lastUpdated] = System.currentTimeMillis()
                    }
                    DatabaseResult.Success
                } else {
                    DatabaseResult.Already
                }
            }
        } catch (e: Exception) {
            DatabaseResult.Failure(e.message ?: "Unknown error")
        }
    }

    override fun hasSkin(playerName: String): DatabaseResult<Nothing> {
        return try {
            transaction(db) {
                val exists = SkinsTable.selectAll()
                    .where { SkinsTable.playerName eq playerName }
                    .singleOrNull() != null

                if (exists) DatabaseResult.Success else DatabaseResult.NotFound
            }
        } catch (e: Exception) {
            DatabaseResult.Failure(e.message ?: "Unknown error")
        }
    }

    override fun getSkin(playerName: String): DatabaseResult<PlayerSkin> {
        return try {
            transaction(db) {
                val resultRow = SkinsTable.selectAll()
                    .where { SkinsTable.playerName eq playerName }
                    .singleOrNull()

                if (resultRow != null) {
                    DatabaseResult.GetSuccess(
                        PlayerSkin(
                            value = resultRow[SkinsTable.skinValue],
                            signature = resultRow[SkinsTable.skinSignature]
                        )
                    )
                } else {
                    DatabaseResult.NotFound
                }
            }
        } catch (e: Exception) {
            DatabaseResult.Failure(e.message ?: "Unknown error")
        }
    }

    override fun updateSkin(playerName: String, value: String, signature: String): DatabaseResult<Nothing> {
        return try {
            transaction(db) {
                val updated = SkinsTable.update({ SkinsTable.playerName eq playerName }) {
                    it[skinValue] = value
                    it[skinSignature] = signature
                    it[lastUpdated] = System.currentTimeMillis()
                }

                if (updated > 0) DatabaseResult.Success else DatabaseResult.NotFound
            }
        } catch (e: Exception) {
            DatabaseResult.Failure(e.message ?: "Unknown error")
        }
    }

    override fun deleteSkin(playerName: String): DatabaseResult<Nothing> {
        return try {
            transaction(db) {
                val deleted = SkinsTable.deleteWhere { SkinsTable.playerName eq playerName }
                if (deleted > 0) DatabaseResult.Success else DatabaseResult.NotFound
            }
        } catch (e: Exception) {
            DatabaseResult.Failure(e.message ?: "Unknown error")
        }
    }

    private fun existsSkin(playerName: String): Boolean {
        return SkinsTable.selectAll()
            .where { SkinsTable.playerName eq playerName }
            .singleOrNull() != null
    }

}