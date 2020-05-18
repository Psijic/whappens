package com.psvoid.whappens.data

class CountriesRepository(private val countriesDao: CountriesDao) {

    suspend fun insert(countries: List<CountryData>) = countriesDao.insert(countries)
    suspend fun insert(country: CountryData) = countriesDao.insert(country)

    fun getAll() = countriesDao.getAll()

}