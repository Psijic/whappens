/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.psvoid.whappens.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.psvoid.whappens.model.ClusterMarker
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.util.*

/**
 * Helper class to read in cluster items from a resource
 */
class MyItemReader {

    /**
     * Returns a list of cluster items read from the provided [inputStream]
     */
    fun read(inputStream: InputStream): List<ClusterMarker> {
        // This matches only once in whole input so Scanner.next returns whole InputStream as a
        // String. http://stackoverflow.com/a/5445161/2183804
        val REGEX_INPUT_BOUNDARY_BEGINNING = "\\A"

        val items = mutableListOf<ClusterMarker>()
        val json = Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next()
        val array = JSONArray(json)
        try {
            for (i in 0 until array.length()) {
                var title: String? = null
                var snippet: String? = null
                val `object` = array.getJSONObject(i)
                val lat = `object`.getDouble("lat")
                val lng = `object`.getDouble("lng")
                if (!`object`.isNull("title")) {
                    title = `object`.getString("title")
                }
                if (!`object`.isNull("snippet")) {
                    snippet = `object`.getString("snippet")
                }
                items.add(ClusterMarker(lat, lng, title, snippet))
            }
        } catch (e: JSONException) {
            Log.e("MyItemReader", "Error reading list of markers.", e)
        }
        return items
    }

    @Serializable
    data class Data(val a: Int, val b: String = "42")

    fun main() {
        // Json also has .Default configuration which provides more reasonable settings,
        // but is subject to change in future versions
        val json = Json(JsonConfiguration.Stable)
        // serializing objects
        val jsonData = json.stringify(Data.serializer(), Data(42))
        // serializing lists
//    val jsonList = json.stringify(Data.serializer().list, listOf(Data(42)))
        val jsonList = json.stringify(Data.serializer().list, listOf(Data(42)))
        println(jsonData) // {"a": 42, "b": "42"}
        println(jsonList) // [{"a": 42, "b": "42"}]

        // parsing data back
        val obj = json.parse(Data.serializer(), """{"a":42}""") // b is optional since it has default value
        println(obj) // Data(a=42, b="42")
    }
}
