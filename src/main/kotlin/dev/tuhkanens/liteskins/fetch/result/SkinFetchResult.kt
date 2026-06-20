package dev.tuhkanens.liteskins.fetch.result

sealed interface SkinFetchResult<out T> {
    data class GetSuccess<T>(val data: T): SkinFetchResult<T>
    data class Failure(val error: String): SkinFetchResult<Nothing>
}