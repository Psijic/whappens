package com.psvoid.whappens.viewmodels

import android.app.Application
import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.psvoid.whappens.network.Config

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private companion object {
        const val TAG = "MainViewModel"
    }

    private val countryCode: String

    init {
        val tm = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        countryCode = tm.networkCountryIso
        Config.countries = listOf(countryCode)
        Log.i(TAG, "User country code: $countryCode")
    }
}