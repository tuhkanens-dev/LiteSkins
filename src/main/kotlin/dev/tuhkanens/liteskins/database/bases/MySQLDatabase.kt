package dev.tuhkanens.liteskins.database.bases

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.tuhkanens.liteskins.database.SkinsDatabase
import dev.tuhkanens.liteskins.manager.ConfigManager
import org.jetbrains.exposed.sql.Database

object MySQLDatabase : SkinsDatabase() {

    private var dataSource: HikariDataSource? = null

    override fun createConnection(): Database {
        val node = ConfigManager.get().node("database")

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://${node.node("host").getString("localhost")}:${node.node("port").getInt(3306)}/${node.node("database").getString("skins")}?useSSL=false&characterEncoding=utf8"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = node.node("user").getString("root") ?: "root"
            password = node.node("password").getString("") ?: ""
            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 30_000
            idleTimeout = 600_000
            maxLifetime = 1_800_000
        }

        val hikariDataSource = HikariDataSource(config)
        dataSource = hikariDataSource

        return Database.connect(hikariDataSource)
    }

    override fun closeConnection() {
        dataSource?.close()
        dataSource = null
    }

}