package io.horizontalsystems.bankwallet

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
    data class Loading<out T>(val data: T? = null) : Result<T>
    data class Success<out T>(val data: T) : Result<T>
    data class Error<out T>(val exception: Throwable, val data: T? = null) : Result<T>
}

val <T> Result<T>?.isLoading
    get() = this is Result.Loading

val <T> Result<T>?.isSuccess
    get() = this is Result.Success

val <T> Result<T>?.isError
    get() = this is Result.Error

val <T> Result<T>.data: T?
    get() = when (this) {
        is Result.Loading -> data
        is Result.Success -> data
        is Result.Error -> data
    }

val <T> Result<T>.exception
    get() = this.takeAs<Result.Error<T>>()?.exception

fun <T> Result<T>.toUnit(): Result<Unit> =
    when (this) {
        is Result.Loading -> Result.Loading(Unit)
        is Result.Success -> Result.Success(Unit)
        is Result.Error -> Result.Error(exception, Unit)
    }

fun <T, E> Result<T>.mapDataSync(mapper: (T) -> E): Result<E> =
    when (this) {
        is Result.Loading -> Result.Loading(data?.let { mapper(it) })
        is Result.Success -> Result.Success(mapper(data))
        is Result.Error -> Result.Error(exception, data?.let { mapper(it) })
    }

suspend fun <T, E> Result<T>.mapData(mapper: suspend (T) -> E): Result<E> =
    when (this) {
        is Result.Loading -> Result.Loading(data?.let { mapper(it) })
        is Result.Success -> Result.Success(mapper(data))
        is Result.Error -> Result.Error(exception, data?.let { mapper(it) })
    }

fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading()) }
        .catch { e -> emit(Result.Error(e)) }

fun <T> asFlowResult(action: suspend () -> T): Flow<Result<T>> =
    flow {
        emit(Result.Loading())
        try {
            emit(Result.Success(action()))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

fun <T, E> Flow<Result<T>>.mapResultData(mapper: suspend (T) -> E): Flow<Result<E>> =
    this.map { it.mapData(mapper) }

fun <T> Flow<Result<T>>.onResultError(action: suspend (Throwable) -> Unit): Flow<Result<T>> =
    this.onEach { resource -> resource.exception?.let { action(it) } }
