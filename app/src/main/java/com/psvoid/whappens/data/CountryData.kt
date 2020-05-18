package com.psvoid.whappens.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries_table")
data class CountryData(
    @PrimaryKey val country_code: String = "",
    val timestamp: Long = System.currentTimeMillis()
)