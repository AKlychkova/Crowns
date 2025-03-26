package ru.hse.crowns.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ru.hse.crowns.databinding.FragmentGameBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.adapters.KillerSudokuBoardRecyclerAdapter
import ru.hse.crowns.generation.dancingLinks.KillerSudokuDLUniqueChecker
import ru.hse.crowns.generation.killerSudoku.KillerSudokuBacktrackingSolutionGenerator
import ru.hse.crowns.generation.killerSudoku.KillerSudokuGenerator

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gen = KillerSudokuGenerator(
            0,
            KillerSudokuDLUniqueChecker(),
            KillerSudokuBacktrackingSolutionGenerator()
        )

        binding.boardRecyclerView.layoutManager = GridLayoutManager(context, 9)
        val board = gen.generate()
        binding.boardRecyclerView.adapter = KillerSudokuBoardRecyclerAdapter(board)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}