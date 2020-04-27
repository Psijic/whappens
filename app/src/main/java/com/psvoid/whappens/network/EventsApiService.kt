package com.psvoid.whappens.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.psvoid.whappens.map.MarsProperty
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.model.StreetEvent
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://api.eventful.com/json/events/" //"https://api.github.com/"
//private const val BASE_URL = "https://mars.udacity.com/"
private val contentType: MediaType = MediaType.get("application/json")

//private val contentType = MediaType("application/json")
val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true, isLenient = true))

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for full Kotlin compatibility.
 */
//private val moshi = Moshi.Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()

private val logging: Interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS);
private val httpClient =  OkHttpClient.Builder().addInterceptor(logging).build()


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi object.
 */
private val retrofit = Retrofit.Builder()
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(json.asConverterFactory(contentType))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

/**
 * A public interface that exposes the [getEvents] method
 */
interface EventsApiService {
    /**
     * Returns a Coroutine [Deferred] [List] of [StreetEvent] which can be fetched with await() if in a Coroutine scope.
     * The @GET annotation indicates that endpoint will be requested with the GET HTTP method
     */
    @GET("search?app_key=hFc7MXpW2X4ZnCqr&location=london&page_size=10&include=categories,subcategories,popularity,price&date=Future&image_sizes=thumb,block250")
//    @GET("realestate?size=450&gg=543")
    fun getEvents():
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
            Deferred<List<ClusterMarker>>
//            Deferred<List<MarsProperty>>
//            Deferred<List<Any>>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object EventsApi {
    val retrofitService: EventsApiService by lazy { retrofit.create(EventsApiService::class.java) }
}
