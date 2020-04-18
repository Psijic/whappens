package com.psvoid.whappens.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class StreetEvent(
    val name: String,
    val type: String,
    val id: String,
    val url: String,
    val locale: String,
    val images: List<EventImage>,
    val dateTime: Date,
    val price: Price,
    val classifications: Classification,
    val place: Place
)

data class Place(
    val city: String,
    val country: String,
    val countryCode: String,
    val address: String,
    val location: Coordinates,
    val externalLinks: Links
)

data class Links(
    val name: String,
    val urls: List<String>
)

data class Coordinates(
    val lat: Double,
    val lng: Double
)

data class Classification(
    val primary: Boolean,
    val family: Boolean,
    val segment: IdName,
    val genre: IdName,
    val subGenre: IdName,
    val type: IdName,
    val subType: IdName
)

data class IdName(
    val id: String,
    val name: String
)

data class Price(
    val min: Float,
    val max: Float,
    val currency: String
)

data class EventImage(
    val ratio: String,
    val url: String,
    val width: Int,
    val height: Int,
    val fallback: Boolean
)