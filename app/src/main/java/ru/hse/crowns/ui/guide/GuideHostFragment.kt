package ru.hse.crowns.ui.guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.addCallback
import com.google.android.material.tabs.TabLayout
import ru.hse.crowns.R
import ru.hse.crowns.databinding.FragmentGuideHostBinding

class GuideHostFragment : Fragment() {

    private var _binding: FragmentGuideHostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuideHostBinding.inflate(inflater, container, false)

        // Switch tab colors and guide fragments
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    1 -> {
                        binding.tabLayout.tabTextColors =
                            resources.getColorStateList(R.color.nav_bar_queens_tint, null)
                        binding.tabLayout.setSelectedTabIndicatorColor(
                            resources.getColor(R.color.queens, null)
                        )
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, QueensGuideFragment())
                            .commit()
                    }
                    0 -> {
                        binding.tabLayout.tabTextColors =
                            resources.getColorStateList(R.color.nav_bar_n_queens_tint, null)
                        binding.tabLayout.setSelectedTabIndicatorColor(
                            resources.getColor(R.color.n_queens, null)
                        )
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, NQueensGuideFragment())
                            .commit()
                    }
                    2 -> {
                        binding.tabLayout.tabTextColors =
                            resources.getColorStateList(R.color.nav_bar_killer_sudoku_tint, null)
                        binding.tabLayout.setSelectedTabIndicatorColor(
                            resources.getColor(R.color.killer_sudoku, null)
                        )
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, KillerSudokuGuideFragment())
                            .commit()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val button: ImageButton? = requireActivity().findViewById(R.id.guideImageButton)
            button?.callOnClick()
        }
        callback.isEnabled = true

        return binding.root
    }
}