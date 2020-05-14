package com.psvoid.whappens.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.psvoid.whappens.model.ClusterMarker

/** Defines methods for using the ClusterMarker class with Room. */
@Dao
interface MarkerDatabaseDao {

    @Insert
    fun insert(marker: ClusterMarker)

    /**
     * Update a row with a value already set in a column, replace the old value with the new one.
     * @param markers new value to write
     */
    @Update
    fun update(markers: List<ClusterMarker>)

    /**
     * Select and returns the row that matches the supplied start time, which is our key.
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from markers_table WHERE id = :key")
    fun get(key: String): ClusterMarker?

    /**
     * Delete all values from the table. This does not delete the table.
     */
    @Query("DELETE FROM markers_table")
    fun clear()

    /**
     * Select and returns all rows in the table.
     */
    @Query("SELECT * FROM markers_table")
    fun getAllMarkers(): LiveData<List<ClusterMarker>>

    /**
     * Select and returns all rows in the table.
     */
    @Query("SELECT * FROM markers_table WHERE country_abbr = :countryAbbr")
    fun getAllMarkersByCountry(countryAbbr:String): LiveData<List<ClusterMarker>>
}