package com.psvoid.whappens.data

import androidx.lifecycle.LiveData
import androidx.room.*

/** Defines methods for using the ClusterMarker class with Room. */
@Dao
interface MarkerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(marker: ClusterMarker)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(markers: List<ClusterMarker>)

    /**
     * Update a row with a value already set in a column, replace the old value with the new one.
     * @param markers new value to write
     */
    @Update
    fun update(markers: List<ClusterMarker>)

    /** Select and returns the row that matches the key. */
    @Query("SELECT * from markers_table WHERE id = :key")
    fun get(key: String): ClusterMarker?

    /** Delete all values from the table. This does not delete the table. */
    @Query("DELETE FROM markers_table")
    suspend fun clear()

    /** Select and returns all rows in the table. */
    @Query("SELECT * FROM markers_table")
    fun getAllMarkers(): LiveData<List<ClusterMarker>>

    /** Select and returns all rows in the table. */
    @Query("SELECT * FROM markers_table WHERE country_abbr = :countryAbbr")
    fun getAllMarkersByCountry(countryAbbr: String): LiveData<List<ClusterMarker>>
}