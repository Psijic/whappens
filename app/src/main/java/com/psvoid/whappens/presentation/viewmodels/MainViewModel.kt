package com.psvoid.whappens.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.telephony.TelephonyManager
import androidx.lifecycle.AndroidViewModel
import com.psvoid.whappens.data.network.Config
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private companion object {
    }

    private val countryCode: String

    init {
        val tm = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        countryCode = tm.networkCountryIso
        Config.countries = listOf(countryCode)
        Timber.i("User country code: $countryCode")
    }
}