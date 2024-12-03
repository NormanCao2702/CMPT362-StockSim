package com.example.cmpt362_stocksim

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cmpt362_stocksim.databinding.ActivityMainBinding
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch initial user data
        fetchInitialUserData()

        // Reference to the Bottom Navigation View
        val navView: BottomNavigationView = binding.navView

        // Set up Navigation Controller with the NavHostFragment
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_social,
                R.id.navigation_portfolio
            )
        )
        // Set up the ActionBar to work with the Navigation Controller
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Set up the BottomNavigationView with the Navigation Controller
        navView.setupWithNavController(navController)
    }

    // Function to fetch and refresh user data
    private fun fetchInitialUserData() {
        lifecycleScope.launch {
            try {
                val userDataManager = UserDataManager(this@MainActivity)
                userDataManager.refreshUserInfo()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}