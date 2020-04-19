package com.psvoid.whappens.utils

import android.util.Log
import com.psvoid.whappens.model.ClusterMarker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.util.*

/**
 * Helper class to read in cluster items from a resource
 */
class HelperItemReader {

    /**
     * Returns a list of cluster items read from the provided [inputStream]
     */
    fun read(inputStream: InputStream): List<ClusterMarker> {
        // This matches only once in whole input so Scanner.next returns whole InputStream as a
        // String. http://stackoverflow.com/a/5445161/2183804

        val items = mutableListOf<ClusterMarker>()
        val json = Scanner(inputStream).useDelimiter("\\A").next()
        val array = JSONArray(json)
        try {
            for (i in 0 until array.length()) {
                var title = ""
                var snippet = ""
                val jObject = array.getJSONObject(i)
                val lat = jObject.getDouble("lat")
                val lng = jObject.getDouble("lng")
                if (!jObject.isNull("title")) {
                    title = jObject.getString("title")
                }
                if (!jObject.isNull("snippet")) {
                    snippet = jObject.getString("snippet")
                }
                items.add(ClusterMarker(lat, lng, title, snippet))
            }
        } catch (e: JSONException) {
            Log.e("MyItemReader", "Error reading list of markers.", e)
        }
        return items
    }

    fun readSerializable(inputStream: InputStream): List<ClusterMarker> {
        val items = mutableListOf<ClusterMarker>()
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        val jsonString = inputStreamToString(inputStream)
        val array = JSONArray(jsonString)
        for (i in 0 until array.length()) {
            val jObject = array.getJSONObject(i)
            items.add(json.parse(ClusterMarker.serializer(), jObject.toString()))
        }
        return items
    }

    private fun inputStreamToString(inputStream: InputStream): String {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }
}
