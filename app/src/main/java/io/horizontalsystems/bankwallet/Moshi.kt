package io.horizontalsystems.bankwallet

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

inline fun <reified T : Any> Moshi.fromJson(value: String): T? = adapter<T>().fromJson(value)

inline fun <reified T : Any> Moshi.toJson(value: T): String = adapter<T>().toJson(value)

inline fun <reified T : Any> Moshi.fromJson(input: InputStream): T? =
    adapter<T>().fromJson(input.source().buffer())

inline fun <reified T : Any> Moshi.fromJson(bufferedSource: BufferedSource): T? =
    adapter<T>().fromJson(bufferedSource)

inline fun <reified T : Any> Moshi.toJson(output: OutputStream, value: T?) {
    output.sink().buffer().let { out ->
        adapter<T>().toJson(out, value)
        out.flush()
    }
}

inline fun <reified T : Any> Moshi.adapter(): JsonAdapter<T> = adapter(T::class.java)
