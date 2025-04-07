package ru.hse.crowns.ui.home.killerSudokuHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.R
import ru.hse.crowns.databinding.FragmentKillerSudokuHomeBinding
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel

class KillerSudokuHomeFragment : Fragment() {

    private var _binding: FragmentKillerSudokuHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel : KillerSudokuHomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKillerSudokuHomeBinding.inflate(inflater, container, false)

        binding.startKillerSudokuButton.setOnClickListener { view ->
            val bundle = Bundle()
            when (binding.difficultyRadioGroup.checkedRadioButtonId) {
                R.id.easyRadioButton -> {
                    bundle.putInt("difficultyLevel", KillerSudokuDifficultyLevel.EASY.ordinal)
                }
                R.id.mediumRadioButton -> {
                    bundle.putInt("difficultyLevel", KillerSudokuDifficultyLevel.MEDIUM.ordinal)
                }
                R.id.difficultRadioButton -> {
                    bundle.putInt("difficultyLevel", KillerSudokuDifficultyLevel.DIFFICULT.ordinal)
                }
            }

            view.findNavController()
                .navigate(R.id.action_navigation_killer_sudoku_to_killerSudokuGameFragment, bundle)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}