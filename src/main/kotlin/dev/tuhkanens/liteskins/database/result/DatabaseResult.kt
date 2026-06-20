package dev.tuhkanens.liteskins.database.result

sealed interface DatabaseResult<out T> {
    data class GetSuccess<T>(val data: T): DatabaseResult<T>
    data class Failure(val error: String): DatabaseResult<Nothing>

    object Already : DatabaseResult<Nothing>
    object Success : DatabaseResult<Nothing>
    object NotFound : DatabaseResult<Nothing>
}