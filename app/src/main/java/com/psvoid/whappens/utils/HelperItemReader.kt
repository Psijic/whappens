package com.psvoid.whappens.utils

import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.model.adapters.TM
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.InputStream

/**
 * Helper class to read in cluster items from a resource
 */
class HelperItemReader {

    fun readSerializable(inputStream: InputStream): List<ClusterMarker> {
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        val jsonString = inputStreamToString(inputStream)
        val items = json.parse(TM.serializer, jsonString)
//        val items = json.parse(ClusterMarker.serializer().list, all.toString())
//        val items = json.parse(ClusterMarker.serializer().list, jsonString)
        return items._embedded.events
    }

    private fun inputStreamToString(inputStream: InputStream): String {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }
}
