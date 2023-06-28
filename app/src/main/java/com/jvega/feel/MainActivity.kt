package com.jvega.feel

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.jvega.feel.network.RetrofitClient
import com.jvega.feel.util.DialogHelper
import com.jvega.feel.util.TokenManager
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        toggle.drawerArrowDrawable.color = Color.WHITE
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_messages -> {
                    Log.d("MainActivity", "To messages fragment")
                    navController.navigate(R.id.messagesFragment)
                }
                R.id.nav_chat -> {
                    Log.d("MainActivity", "To chat fragment")
                    navController.navigate(R.id.chatFragment)
                }
            }
            toggle.drawerArrowDrawable.color = Color.WHITE
            toggle.syncState()
            true
        }
        lifecycleScope.launch {
            checkToken()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private suspend fun checkToken(){
        val apiService = RetrofitClient.apiService
        try {
            val response = apiService.validateToken(TokenManager.getToken().toString())
            if (response.isSuccessful) {
                val validationResponse = response.body()
                val isValid = validationResponse?.valid ?: false
                if (isValid) {
                    Log.d("MainActivity", "Token is valid")
                } else {
                    DialogHelper.showAlertDialog(this, "Session Over", "Please renew your session.")
                }
            } else {
                DialogHelper.showAlertDialog(this, "Server error", "Please try to reconnect.")
            }
        } catch (e: IOException) {
            // Handle network error
            DialogHelper.showAlertDialog(this, "Network error", "Please check your internet connection.")
        } catch (e: Exception) {
            // Handle other types of errors
            DialogHelper.showAlertDialog(this, "Error", "Looks like the server is unreachable.")
        }
    }
}


