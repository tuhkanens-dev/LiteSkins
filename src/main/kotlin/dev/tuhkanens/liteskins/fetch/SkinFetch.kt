package dev.tuhkanens.liteskins.fetch

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.tuhkanens.liteskins.data.PlayerSkin
import dev.tuhkanens.liteskins.fetch.result.SkinFetchResult
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object SkinFetch {

    private val httpClient: HttpClient = HttpClient.newHttpClient()

    fun fetch(playerName: String): SkinFetchResult<PlayerSkin> {
        return try {

            val uuid: String = this.fetchJson("${SkinAPI.MOJANG_PROFILE_API.url}$playerName").get("id").asString
            val profile: JsonObject = this.fetchJson("${SkinAPI.MOJANG_SKIN_API.url}$uuid?unsigned=false")
            val property: JsonObject = profile.getAsJsonArray("properties").get(0).asJsonObject

            val value: String = property.get("value").asString
            val signature: String = property.get("signature").asString

            SkinFetchResult.GetSuccess(PlayerSkin(value, signature))

        } catch (e: Exception) {
            e.printStackTrace()
            SkinFetchResult.Failure(e.message ?: "Unknown error")
        }
    }

    private fun fetchJson(url: String): JsonObject {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "SkinsLite-Skin-Fetcher")
            .GET()
            .build()

        val response: HttpResponse<String> = httpClient.send<String>(request, HttpResponse.BodyHandlers.ofString())
        return JsonParser.parseString(response.body()).asJsonObject
    }

}