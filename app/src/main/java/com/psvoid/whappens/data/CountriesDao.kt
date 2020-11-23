package com.psvoid.whappens.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CountriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: CountryData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countries: List<CountryData>)

    @Update
    suspend fun update(countries: List<CountryData>)

    /** Delete all values from the table. This does not delete the table. */
    @Query("DELETE FROM countries_table")
    suspend fun clear()

    /** Select and returns all rows in the table. */
    @Query("SELECT * FROM countries_table")
    suspend fun getAll(): List<CountryData>

    @Query("SELECT * FROM countries_table WHERE country_code = :country_code")
    suspend fun getByCountry(country_code: String): CountryData?
}