package com.masjid.core.domain

/**
 * Sealed class representing the result of any operation.
 * Used throughout the domain/data layers to propagate results
 * without throwing exceptions across boundaries.
 */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun errorMessageOrNull(): String? = when (this) {
        is Success -> null
        is Error -> message
    }

    fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, cause)
    }

    suspend fun <R> suspendMap(transform: suspend (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, cause)
    }

    companion object {
        /**
         * Wraps a suspending block in a try-catch, returning AppResult.
         */
        suspend fun <T> runCatching(block: suspend () -> T): AppResult<T> {
            return try {
                Success(block())
            } catch (e: Exception) {
                Error(e.message ?: "Unknown error", e)
            }
        }
    }
}
