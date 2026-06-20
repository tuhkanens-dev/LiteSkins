package dev.tuhkanens.liteskins.manager

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.tuhkanens.liteskins.Main
import dev.tuhkanens.liteskins.data.PlayerSkin
import dev.tuhkanens.liteskins.fetch.SkinAPI
import dev.tuhkanens.liteskins.fetch.SkinFetch
import dev.tuhkanens.liteskins.fetch.result.SkinFetchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

object DefaultSkinsManager {

    private val plugin: Main = Main.instance
    private val skins = mutableListOf<PlayerSkin>()

    suspend fun loadSkins() {
        val cacheFile = File("${plugin.directory}/default-skins-cache.json")

        if (cacheFile.exists()) {
            val json = JsonParser.parseString(cacheFile.readText()).asJsonArray
            json.forEach { element ->
                val obj = element.asJsonObject
                skins.add(PlayerSkin(
                    value = obj.get("value").asString,
                    signature = obj.get("signature").asString
                ))
            }
            plugin.logger.info("Loaded ${skins.size} default skins from cache.")
            return
        }

        val ids = ConfigManager.get()
            .node("default-skins")
            .getList(String::class.java) ?: emptyList()

        val names = ConfigManager.get()
            .node("default-skins-per-names")
            .getList(String::class.java) ?: emptyList()

        val results = coroutineScope {
            val byId = ids.map { id ->
                async(Dispatchers.IO) {
                    try { fetchSkinById(id) } catch (e: Exception) {
                        plugin.logger.warn("Failed to load skin $id: ${e.message}")
                        null
                    }
                }
            }
            val byName = names.map { name ->
                async(Dispatchers.IO) {
                    try { fetchSkinByName(name) } catch (e: Exception) {
                        plugin.logger.warn("Failed to load skin for $name: ${e.message}")
                        null
                    }
                }
            }
            (byId + byName).awaitAll()
        }

        results.filterNotNull().forEach { skins.add(it) }

        val cacheArray = JsonArray()
        skins.forEach { skin ->
            val obj = JsonObject()
            obj.addProperty("value", skin.value)
            obj.addProperty("signature", skin.signature)
            cacheArray.add(obj)
        }
        cacheFile.writeText(cacheArray.toString())

        plugin.logger.info("Loaded ${skins.size}/${ids.size} default skins.")
    }

    private suspend fun fetchSkinById(id: String): PlayerSkin? = withContext(Dispatchers.IO) {
        repeat(3) { attempt ->
            try {
                val connection = URI("${SkinAPI.MINESKIN_SKIN_API.url}$id")
                    .toURL()
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "SkinsLite-Skin-Fetcher")
                connection.connectTimeout = 10_000
                connection.readTimeout = 30_000

                if (connection.responseCode != 200) {
                    plugin.logger.warn("MineSkin returned ${connection.responseCode} for $id")
                    return@withContext null
                }

                val json = JsonParser.parseString(
                    connection.getInputStream().bufferedReader().readText()
                ).asJsonObject
                val texture = json.getAsJsonObject("data").getAsJsonObject("texture")

                return@withContext PlayerSkin(
                    value = texture.get("value").asString,
                    signature = texture.get("signature").asString
                )
            } catch (e: Exception) {
                plugin.logger.warn("Failed to load skin $id (attempt ${attempt + 1}: ${e.message}")
                if (attempt < 2) delay(2000)
            }
        }
        null
    }

    private suspend fun fetchSkinByName(name: String): PlayerSkin? = withContext(Dispatchers.IO) {
        repeat(3) { attempt ->
            try {
                val result = SkinFetch.fetch(name)
                if (result is SkinFetchResult.GetSuccess) {
                    return@withContext result.data
                }
            } catch (e: Exception) {
                plugin.logger.warn("Failed to load skin for $name (attempt ${attempt + 1}): ${e.message}")
                if (attempt < 2) delay(2000)
            }
        }
        null
    }

    fun randomDefaultSkin() = skins.randomOrNull()

}