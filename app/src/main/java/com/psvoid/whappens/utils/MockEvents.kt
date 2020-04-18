package com.psvoid.whappens.utils

import android.util.Log
import com.psvoid.whappens.model.StreetEvent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

class MockEvents {


    companion object {


        @JsonClass(generateAdapter = true)
        data class Movie (
            @Json(name = "vote_count") val voteCount: Int = -1,
            val id: Int,
            val title: String,
            @Json(name = "image_path") val imagePath: String,
            @Json(name = "genre_ids") val genres: List<Genre>,
            val overview: String
        )

        @JsonClass(generateAdapter = true)
        data class Genre(val id: Int, val name: String)

        var Events: List<StreetEvent> = listOf()

        fun GenerateFromJson() {

//            val moshi = Moshi.Builder()
//                .add(KotlinJsonAdapterFactory())
//                .build()


            val moshi: Moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<Movie> = moshi.adapter(Movie::class.java)
            val movie = adapter.fromJson("{\"vote_count\": 2026}")
            Log.i("movie", movie.toString())
        }
    }
}