package ru.hse.crowns.ui.game.killerSudokuGame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.hse.crowns.databinding.FragmentKillerSudokuGameBinding
import org.koin.core.parameter.parametersOf
import ru.hse.crowns.R
import ru.hse.crowns.adapters.KillerSudokuBoardRecyclerAdapter
import ru.hse.crowns.domain.boards.BoardObserver
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel

class KillerSudokuGameFragment() : Fragment() {
    private var _binding: FragmentKillerSudokuGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: KillerSudokuGameViewModel
    private lateinit var boardAdapter: KillerSudokuBoardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKillerSudokuGameBinding.inflate(inflater, container, false)

        // define viewmodel
        val level: KillerSudokuDifficultyLevel = arguments?.getInt("difficultyLevel")?.let {
            KillerSudokuDifficultyLevel.entries[it]
        } ?: KillerSudokuDifficultyLevel.MEDIUM
        viewModel = getViewModel { parametersOf(level.getMaxToDelete()) }

        // define recycler view
        binding.board.recyclerView.layoutManager = object : GridLayoutManager(context, 9) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        boardAdapter = KillerSudokuBoardRecyclerAdapter({ row, column ->
            viewModel.onCellClick(
                row,
                column,
                getCurrentValue(),
                binding.eraseToggleButton.isChecked,
                !binding.noteSwitch.isChecked)
        })
        binding.board.recyclerView.adapter = boardAdapter

        observeViewModel()

        return binding.root
    }

    private fun getCurrentValue(): Int {
        return when(binding.valueRadioGroup.checkedRadioButtonId) {
            R.id.radioButton1 -> 1
            R.id.radioButton2 -> 2
            R.id.radioButton3 -> 3
            R.id.radioButton4 -> 4
            R.id.radioButton5 -> 5
            R.id.radioButton6 -> 6
            R.id.radioButton7 -> 7
            R.id.radioButton8 -> 8
            R.id.radioButton9 -> 9
            // unreachable
            else -> 0
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.content.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.content.visibility = View.VISIBLE
            }
        }

        viewModel.boardLD.observe(viewLifecycleOwner) {
            it.addObserver(object : BoardObserver {
                override fun onChanged(row: Int, column: Int) {
                    boardAdapter.updateCellValue(row, column)
                }
            })
            boardAdapter.setBoard(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateBoard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.boardLD.value?.clearObservers()
        _binding = null
    }
}