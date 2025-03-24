package ru.hse.crowns

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.hse.crowns.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation between home screens
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        // Set up colors change
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_n_queens -> {
                    binding.navView.itemIconTintList =
                        resources.getColorStateList(R.color.nav_bar_n_queens_tint, null)
                    binding.navView.itemTextColor =
                        resources.getColorStateList(R.color.nav_bar_n_queens_tint, null)
                    binding.navView.itemRippleColor =
                        resources.getColorStateList(R.color.nav_bar_n_queens_tint, null)
                }

                R.id.navigation_queens -> {
                    binding.navView.itemIconTintList =
                        resources.getColorStateList(R.color.nav_bar_queens_tint, null)
                    binding.navView.itemTextColor =
                        resources.getColorStateList(R.color.nav_bar_queens_tint, null)
                    binding.navView.itemRippleColor =
                        resources.getColorStateList(R.color.nav_bar_queens_tint, null)
                }

                R.id.navigation_killer_sudoku -> {
                    binding.navView.itemIconTintList =
                        resources.getColorStateList(R.color.nav_bar_killer_sudoku_tint, null)
                    binding.navView.itemTextColor =
                        resources.getColorStateList(R.color.nav_bar_killer_sudoku_tint, null)
                    binding.navView.itemRippleColor =
                        resources.getColorStateList(R.color.nav_bar_killer_sudoku_tint, null)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, _, arguments ->
            if(arguments?.getBoolean("ShowBottomBar", false) == true) {
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.navView.visibility = View.GONE
            }
        }

    }
}