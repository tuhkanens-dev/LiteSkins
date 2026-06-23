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
        try {
            val profileData = fetchJson("${SkinAPI.MOJANG_PROFILE_API.url}$playerName")

            if (!profileData.has("id")) {
                return SkinFetchResult.Failure("Player not found")
            }

            val uuid = profileData.get("id").asString

            val profile = fetchJson("${SkinAPI.MOJANG_SKIN_API.url}$uuid?unsigned=false")

            if (!profile.has("properties")) {
                return SkinFetchResult.Failure("Skin data not found")
            }

            val property = profile
                .getAsJsonArray("properties")
                .first()
                .asJsonObject

            return SkinFetchResult.GetSuccess(
                PlayerSkin(
                    property.get("value").asString,
                    property.get("signature").asString
                )
            )
        } catch (e: Exception) {
            return SkinFetchResult.Failure(e.message ?: "Unknown error")
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