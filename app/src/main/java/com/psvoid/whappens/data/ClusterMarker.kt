package com.psvoid.whappens.data

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.maps.android.clustering.ClusterItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A data class that implements the [ClusterItem] interface so it can be clustered. */
@IgnoreExtraProperties
@Serializable
@Entity(tableName = "markers_table")
data class ClusterMarker(
//    @SerialName("title")
    val name: String = "",
    @PrimaryKey
    val id: String = "",
    val url: String? = null,
    val locale: String = "en",
//    @SerialName("image")
//    @TypeConverters(ImagesConverter::class)
    val image: String? = null, // 250*250 or 4*3 ratio
    @SerialName("start_time") @PropertyName("start_time") @ColumnInfo(name = "start_time")
    val startTime: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    val price: String? = null,
//    val categories: Categories? = null,
//    @TypeConverters(CategoriesConverter::class)
//    val categories: List<IdName>? = null,
//    val popularity: String? = null,
    val popularity: Int? = null,
//    @SerialName("venue_address")
    val address: String? = null,
    val country_name: String = "",
    val country_code: String = "",
    val city: String = "",
    val region: String? = null
//    val performers: Performer
//    val place: Place,

) : ClusterItem {

    override fun getPosition() = LatLng(latitude, longitude)
    override fun getTitle() = name
    override fun getSnippet() = address ?: startTime

    @Serializable
    data class Categories(val category: List<IdName>)

    @Serializable
//    data class Images(val thumb: EventImage, val block250: EventImage)
    data class Images(val block250: EventImage? = null)

    //@Serializable
    //data class Performer(val performer: List<IdName>)
}

//class ImagesConverter {
//    @TypeConverter
//    fun fromImages(images: ClusterMarker.Images?): String {
//        return images?.block250.toString()
//    }
//
//    @TypeConverter
//    fun toImages(data: String): ClusterMarker.Images {
//        val list = data.split(", ")
//        return ClusterMarker.Images(
//            block250 = EventImage(
//                list[0],
//                list[1],
//                list[2]
//            )
//        )
//    }
//}
//
//class CategoriesConverter {
//    @TypeConverter
//    fun fromData(value: List<IdName>): String {
//        return value.toString()
//    }
//
//    @TypeConverter
//    fun toData(data: String): List<IdName> {
//        val list = data.split(" ")
//        return listOf()
//    }
//}



