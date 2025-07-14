package com.example.skinvision

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up Bottom Navigation with NavController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setupWithNavController(navController)

        // Hide bottom navigation on certain destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.welcomeFragment,
                R.id.loginFragment, R.id.signUpFragment,
                R.id.doctorsFragment, R.id.chatFragment,
                R.id.editProfileFragment, R.id.settingsFragment,
                R.id.helpCenterFragment, R.id.scheduleFragment -> {
                    bottomNav?.visibility = View.GONE
                }
                else -> {
                    bottomNav?.visibility = View.VISIBLE
                }
            }
        }
    }
}
