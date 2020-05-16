package com.psvoid.whappens.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Possible to inline factory https://www.albertgao.xyz/2018/04/13/how-to-add-additional-parameters-to-viewmodel-via-kotlin */
@Suppress("UNCHECKED_CAST")
class MapViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java))
            return MapViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}