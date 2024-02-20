package io.horizontalsystems.bankwallet

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.withIndex
import timber.log.Timber

suspend inline fun <T> Flow<T>.safeCollect(
    crossinline onEach: suspend (value: T) -> Unit,
    crossinline onError: suspend (e: Throwable) -> Unit,
) {
    catch { e -> onError(e) }
        .collect {
            try {
                onEach(it)
            } catch (e: Throwable) {
                Timber.e(e)
                onError(e)
            }
        }
}

suspend inline fun <T> Flow<T>.safeCollectLatest(
    crossinline onEach: suspend (value: T) -> Unit,
    crossinline onError: suspend (e: Throwable) -> Unit,
) {
    catch { e -> onError(e) }
        .collectLatest {
            try {
                onEach(it)
            } catch (e: Throwable) {
                onError(e)
            }
        }
}

suspend inline fun <T> Flow<T>.safeCollect(
    crossinline onError: suspend (e: Throwable) -> Unit,
) {
    catch { e -> onError(e) }
        .collect()
}

inline fun <T, R> Flow<T>.distinctMap(crossinline transform: suspend (value: T) -> R) =
    map(transform).distinctUntilChanged()

inline fun <T, R> Flow<T>.distinctMapNotNull(crossinline transform: suspend (value: T) -> R?) =
    mapNotNull(transform).distinctUntilChanged()

inline fun <T> Flow<T>.onEachIndexed(crossinline action: suspend (Int, T) -> Unit): Flow<T> =
    withIndex()
        .onEach { action(it.index, it.value) }
        .map { it.value }

inline fun <T, reified R> Flow<T>.onEachInstance(crossinline action: suspend (R) -> Unit): Flow<T> =
    onEach {
        if (it is R) {
            action(it)
        }
    }

fun <T> Flow<T>.startWithNull(): Flow<T?> =
    map<T, T?> { it }
        .onStart { emit(null) }
