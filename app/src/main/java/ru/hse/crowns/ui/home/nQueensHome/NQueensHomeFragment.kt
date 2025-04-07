package ru.hse.crowns.ui.home.nQueensHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.R
import ru.hse.crowns.databinding.FragmentNQueensHomeBinding

class NQueensHomeFragment : Fragment() {

    private var _binding: FragmentNQueensHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel : NQueensHomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNQueensHomeBinding.inflate(inflater, container, false)

        binding.startNQueensButton.setOnClickListener { view ->
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
                .navigate(R.id.action_navigation_n_queens_to_NQueensGameFragment, bundle)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}