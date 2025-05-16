package ru.hse.crowns.ui.host

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.R
import ru.hse.crowns.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding
    private val viewModel : HostViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation between home screens
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        // Set up guide screen navigation
        binding.guideImageButton.setOnClickListener {
            navController.navigate(R.id.guideFragment)
        }

        // Set up navigation bar color change
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

                R.id.guideFragment -> {
                    binding.guideImageButton.setImageResource(R.drawable.ic_arrow_back_24dp)
                    binding.guideImageButton.setOnClickListener {
                        navController.popBackStack()
                        binding.guideImageButton.setImageResource(R.drawable.ic_question_mark_24dp)
                        binding.guideImageButton.setOnClickListener {
                            navController.navigate(R.id.guideFragment)
                        }
                    }
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

        viewModel.coinsBalance.observe(this) {
            binding.coinBalanceTextView.text = it.toString()
        }
    }


}