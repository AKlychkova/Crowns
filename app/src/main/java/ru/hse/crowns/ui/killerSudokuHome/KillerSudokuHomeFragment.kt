package ru.hse.crowns.ui.killerSudokuHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.R
import ru.hse.crowns.databinding.FragmentKillerSudokuHomeBinding

class KillerSudokuHomeFragment : Fragment() {

    private var _binding: FragmentKillerSudokuHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : KillerSudokuHomeViewModel by viewModel()

    enum class DifficultyLevels {
        EASY,
        MEDIUM,
        DIFFICULT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKillerSudokuHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.startKillerSudokuButton.setOnClickListener { view ->
            val bundle = Bundle()
            when (binding.difficultyRadioGroup.checkedRadioButtonId) {
                R.id.easyRadioButton -> {
                    bundle.putInt("difficultyLevel", DifficultyLevels.EASY.ordinal)
                }
                R.id.mediumRadioButton -> {
                    bundle.putInt("difficultyLevel", DifficultyLevels.MEDIUM.ordinal)
                }
                R.id.difficultrRadioButton -> {
                    bundle.putInt("difficultyLevel", DifficultyLevels.DIFFICULT.ordinal)
                }
            }

            view.findNavController()
                .navigate(R.id.action_navigation_killer_sudoku_to_gameFragment, bundle)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}