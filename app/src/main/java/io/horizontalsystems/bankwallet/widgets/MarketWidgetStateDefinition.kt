package io.horizontalsystems.bankwallet.widgets

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.Strictness
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object MarketWidgetStateDefinition : GlanceStateDefinition<MarketWidgetState> {
    private const val DATA_STORE_FILENAME_PREFIX = "coindex_widget_state_"

    override suspend fun getDataStore(context: Context, fileKey: String) =
        DataStoreFactory.create(
            serializer = MarketWidgetStateSerializer,
            produceFile = {
                getLocation(
                    context,
                    fileKey
                )
            }
        )

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME_PREFIX + fileKey.lowercase())
    }

    object MarketWidgetStateSerializer : Serializer<MarketWidgetState> {
        private val gson by lazy {
            GsonBuilder()
                .setStrictness(Strictness.LENIENT)
                .registerTypeAdapter(MarketWidgetType::class.java, MarketWidgetTypeAdapter())
                .create()
        }

        override val defaultValue = MarketWidgetState(
            loading = true,
            isPlusUser = false
        )

        override suspend fun readFrom(input: InputStream): MarketWidgetState = try {
            val jsonString = input.readBytes().decodeToString()
            gson.fromJson(jsonString, MarketWidgetState::class.java)
        } catch (exception: JsonSyntaxException) {
            throw CorruptionException("Could not read data: ${exception.message}")
        }

        override suspend fun writeTo(t: MarketWidgetState, output: OutputStream) {
            output.use {
                it.write(
                    gson.toJson(t).encodeToByteArray()
                )
            }
        }
    }
}
