package dev.tuhkanens.liteskins.database.bases

import dev.tuhkanens.liteskins.Main
import dev.tuhkanens.liteskins.database.SkinsDatabase
import org.jetbrains.exposed.sql.Database
import java.io.File

object SQLiteDatabase : SkinsDatabase() {

    override fun createConnection(): Database {

        val dbFile = File("${Main.instance.directory}/skins.db")
        if (!dbFile.parentFile.exists()) dbFile.parentFile.mkdirs()

        return Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )

    }

}