package com.psvoid.whappens.utils

import android.util.Log
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import mock


@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "vote_count") val voteCount: Int = -1,
    val id: Int,
    val title: String,
    @Json(name = "image_path") val imagePath: String,
    @Json(name = "genre_ids") val genres: List<Genre>,
    val overview: String
)

@JsonClass(generateAdapter = true)
data class Genre(val id: Int, val name: String)

//Movie genres from the The movie database - https://www.themoviedb.org/
class GenreAdapter {

    @ToJson
    fun toJson(genres: List<Genre>): List<Int> {
        return genres.map { genre -> genre.id }
    }

    @FromJson
    fun fromJson(genreId: Int): Genre {

        when (genreId) {
            28 -> return Genre(28, "Action")
            12 -> return Genre(12, "Adventure")
            16 -> return Genre(16, "Animation")
            35 -> return Genre(35, "Comedy")
            80 -> return Genre(80, "Crime")
            99 -> return Genre(99, "Documentary")
            18 -> return Genre(18, "Drama")
            10751 -> return Genre(10751, "Family")
            14 -> return Genre(14, "Fantasy")
            36 -> return Genre(36, "History")
            27 -> return Genre(27, "Horror")
            10402 -> return Genre(10402, "Music")
            10749 -> return Genre(10749, "Romance")
            9648 -> return Genre(9648, "Mystery")
            878 -> return Genre(878, "Science Fiction")
            10770 -> return Genre(10770, "TV Movie")
            53 -> return Genre(53, "Mystery")
            10752 -> return Genre(10752, "War")
            37 -> return Genre(37, "Western")
            else -> throw JsonDataException("unknown genre id: $genreId")
        }
    }
}

class Movies {
    init {

        val moshi: Moshi = Moshi.Builder()
            .add(GenreAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<Movie> = moshi.adapter(Movie::class.java)
        val movie: Movie? = adapter.fromJson(mock)
        Log.i("movie", movie.toString())

    }
}
