package com.psvoid.whappens.utils

import android.content.res.Resources
import com.psvoid.whappens.R
import com.psvoid.whappens.adapters.Eve
import com.psvoid.whappens.data.ClusterMarker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.InputStream

/** Helper class to read in cluster items from a resource. Uses kotlin serialization */
class HelperItemReader {
    private fun readSerializable(inputStream: InputStream): List<ClusterMarker> {
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true, isLenient = true))
        val jsonString = inputStreamToString(inputStream)
        val items = json.parse(Eve.serializer, jsonString)
        return items.events.event
    }

    private fun inputStreamToString(inputStream: InputStream): String {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

    /* Read local JSON file */
    fun readResourceJson(resources: Resources): List<ClusterMarker> {
        val inputStream = resources.openRawResource(R.raw.event_utah)
        return HelperItemReader().readSerializable(inputStream)
    }
}
