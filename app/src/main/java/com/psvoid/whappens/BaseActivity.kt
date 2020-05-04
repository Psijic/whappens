package com.psvoid.whappens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

open class BaseActivity : FragmentActivity() {
    companion object Permissions {
        const val LOCATION_PERMISSIONS = 1
        const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    }

    /**  Check if a user gave a permission. */
    protected fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    /** Checks if a user gave location and sets location enabled if so. */
    protected fun enableLocation() {
        if (isPermissionGranted(FINE_LOCATION)) setupLocation()
        else ActivityCompat.requestPermissions(this, arrayOf(FINE_LOCATION), LOCATION_PERMISSIONS)
    }

    /**  Callback for the result from requesting permissions. */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Check if location permissions are granted and enable the location data layer.
        if (requestCode == LOCATION_PERMISSIONS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            setupLocation()
        else
            Toast.makeText(this, R.string.need_location_permission, Toast.LENGTH_LONG).show()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    protected open fun setupLocation() {
//        checkDeviceLocationSettingsAndStartGeofence()
    }

/*    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(this@BaseActivity, REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("BaseActivity", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
//                Snackbar.make(this.view, R.string.location_required_error, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok) { checkDeviceLocationSettingsAndStartGeofence() }
//                    .show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
//                addGeofenceForClue()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }*/
}