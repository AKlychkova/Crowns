package ru.hse.crowns

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.hse.crowns.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Set color for selected tab in navigation bar
        navView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.n_queens_tab -> {
                    navView.itemIconTintList =
                        resources.getColorStateList(R.color.bottom_nav_n_queens, null)
                    navView.itemTextColor =
                        resources.getColorStateList(R.color.bottom_nav_n_queens, null)
                }

                R.id.queens_tab -> {
                    navView.itemIconTintList =
                        resources.getColorStateList(R.color.bottom_nav_queens, null)
                    navView.itemTextColor =
                        resources.getColorStateList(R.color.bottom_nav_queens, null)
                }

                R.id.killer_sudoku_tab -> {
                    navView.itemIconTintList =
                        resources.getColorStateList(R.color.bottom_nav_killer_sudoku, null)
                    navView.itemTextColor =
                        resources.getColorStateList(R.color.bottom_nav_killer_sudoku, null)
                }
            }
            true
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}