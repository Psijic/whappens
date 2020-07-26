package com.psvoid.whappens

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        FirebaseApp.initializeApp(this)

        // When enabled, the app writes the data locally to the device and can maintain state while offline,
        // even if the user or operating system restarts the app.

        setSupportActionBar(topAppBar)

//        val navController = findNavController(R.id.map)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        findViewById<Toolbar>(R.id.topAppBar).setupWithNavController(navController, appBarConfiguration)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.mapActivity, R.id.nav_home, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        setupTopAppBar()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    private fun setupTopAppBar() {

        topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            Log.d("MapActivity", "TopAppBar Navigation Clicked")
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.countries -> {
                    true
                }
                R.id.options -> {
                    true
                }
                else -> false
            }
        }
    }
}