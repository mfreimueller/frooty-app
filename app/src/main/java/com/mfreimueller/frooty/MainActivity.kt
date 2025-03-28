package com.mfreimueller.frooty

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mfreimueller.frooty.data.AppContainer
import com.mfreimueller.frooty.data.DefaultAppContainer
import com.mfreimueller.frooty.data.Repository
import com.mfreimueller.frooty.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        fun navigateToHome() {
            instance!!.navigateToHome()
        }
    }

    lateinit var container: AppContainer
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container = DefaultAppContainer()

        lifecycleScope.launch {
            val accessToken = Repository.getAccessToken(dataStore)
            setupNavigation(accessToken != null)
        }
    }

    private fun setupNavigation(isLoggedIn: Boolean) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val graph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        graph.setStartDestination(if (isLoggedIn) R.id.navigation_home else R.id.navigation_login)
        navController.setGraph(graph, null)

        if (isLoggedIn) {
            setupActionBar(navController)
        }

        navView.setupWithNavController(navController)
    }

    private fun setupActionBar(navController: NavController) {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_meals, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun navigateToHome() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        setupActionBar(navController)
        navController.navigate(R.id.navigation_home)
    }
}