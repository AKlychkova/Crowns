package ru.hse.crowns.ui.home.queensHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.R
import ru.hse.crowns.databinding.FragmentQueensHomeBinding

class QueensHomeFragment : Fragment() {

    private var _binding: FragmentQueensHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel : QueensHomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQueensHomeBinding.inflate(inflater, container, false)

        binding.startQueensButton.setOnClickListener { view ->
            val bundle = Bundle()
            when (binding.difficultyRadioGroup.checkedRadioButtonId) {
                R.id.fiveRadioButton -> {
                    bundle.putInt("boardSize", 5)
                }
                R.id.sixRadioButton -> {
                    bundle.putInt("boardSize", 6)
                }
                R.id.sevenRadioButton -> {
                    bundle.putInt("boardSize", 7)
                }
                R.id.eightRadioButton -> {
                    bundle.putInt("boardSize", 8)
                }
                R.id.nineRadioButton -> {
                    bundle.putInt("boardSize", 9)
                }
                R.id.tenRadioButton -> {
                    bundle.putInt("boardSize", 10)
                }
            }

            view.findNavController()
                .navigate(R.id.action_navigation_queens_to_queensGameFragment, bundle)
        }

        binding.resumeQueensButton.setOnClickListener { view ->
            val bundle = Bundle()
            bundle.putBoolean("fromDataStore", true)
            view.findNavController()
                .navigate(R.id.action_navigation_queens_to_queensGameFragment, bundle)
        }

        viewModel.hasGameData.observe(viewLifecycleOwner) {
            if(it) {
                binding.resumeQueensButton.visibility = View.VISIBLE
            } else {
                binding.resumeQueensButton.visibility = View.INVISIBLE
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}