package com.psvoid.whappens

import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.psvoid.whappens.map.MapActivity

class MainActivity : MapActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        FirebaseApp.initializeApp(this)

        // When enabled, the app writes the data locally to the device and can maintain state while offline,
        // even if the user or operating system restarts the app.

    }
}