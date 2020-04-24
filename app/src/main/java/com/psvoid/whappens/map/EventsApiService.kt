package com.psvoid.whappens.map

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.psvoid.whappens.model.StreetEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import kotlinx.coroutines.Deferred

private const val BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=hEeuNHKORp7EjtucWLoLi6B5242DfHtD&city=berlin"
//private const val BASE_URL = "https://developers.google.com/community/gdg/directory/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getEvents] method
 */
interface EventsApiService {
    /**
     * Returns a Coroutine [Deferred] [List] of [StreetEvent] which can be fetched with await() if in a Coroutine scope.
     * The @GET annotation indicates that endpoint will be requested with the GET HTTP method
     */
    @GET("events.json")
    fun getEvents():
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
//            Deferred<List<StreetEvent>>
            Deferred<Any>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object EventsApi {
    val retrofitService : EventsApiService by lazy { retrofit.create(EventsApiService::class.java) }
}
