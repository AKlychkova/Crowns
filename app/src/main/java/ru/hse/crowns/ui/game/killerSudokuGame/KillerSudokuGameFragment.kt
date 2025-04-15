package ru.hse.crowns.ui.game.killerSudokuGame

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.hse.crowns.databinding.FragmentKillerSudokuGameBinding
import org.koin.core.parameter.parametersOf
import ru.hse.crowns.R
import ru.hse.crowns.adapters.KillerSudokuBoardRecyclerAdapter
import ru.hse.crowns.domain.boards.BoardObserver
import ru.hse.crowns.domain.validation.GameStatus
import ru.hse.crowns.domain.validation.KillerSudokuMistake
import ru.hse.crowns.ui.dialogs.WinDialogFragment
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel

class KillerSudokuGameFragment : Fragment() {
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
        val level =
            KillerSudokuDifficultyLevel.entries[requireArguments().getInt("difficultyLevel")]
        viewModel = getViewModel { parametersOf(level.getMaxToDelete()) }

        // define recycler view
        binding.board.recyclerView.layoutManager = object : GridLayoutManager(context, 9) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        boardAdapter = KillerSudokuBoardRecyclerAdapter { row, column ->
            viewModel.onCellClick(
                row,
                column,
                getCurrentValue(),
                binding.eraseToggleButton.isChecked,
                !binding.noteSwitch.isChecked
            )
        }
        binding.board.recyclerView.adapter = boardAdapter

        observeViewModel()

        return binding.root
    }

    private fun getCurrentValue(): Int {
        return when (binding.valueRadioGroup.checkedRadioButtonId) {
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
                stopChronometer()
            } else {
                binding.progressBar.visibility = View.GONE
                binding.content.visibility = View.VISIBLE
                startChronometer()
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

        viewModel.status.observe(viewLifecycleOwner) {
            if (it is KillerSudokuMistake) {
                binding.mistakeMessageTextView.text = it.getMessage()
                boardAdapter.updateMistakes(it.positions.toList())
            } else {
                binding.mistakeMessageTextView.text = ""
                boardAdapter.updateMistakes(emptyList())
            }
            if (it is GameStatus.Win) {
                binding.chronometer.stop()
                viewModel.time = SystemClock.elapsedRealtime() - binding.chronometer.base
                WinDialogFragment(
                    viewModel.calculatePrize(
                        KillerSudokuDifficultyLevel.entries[requireArguments().getInt("difficultyLevel")]
                    )
                ) { _, which: Int ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> viewModel.startNewGame()
                        DialogInterface.BUTTON_NEUTRAL -> findNavController().popBackStack()
                    }
                }.show(childFragmentManager, "WinDialog")
            }
        }

        viewModel.mistakeCounter.observe(viewLifecycleOwner) {
            binding.mistakeCounterTextView.text = it.toString()
        }

        viewModel.hintCounter.observe(viewLifecycleOwner) {
            binding.hintCounterTextView.text = it.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateBoard()
    }

    private fun startChronometer() {
        binding.chronometer.base = SystemClock.elapsedRealtime() - viewModel.time
        binding.chronometer.start()
    }

    private fun stopChronometer() {
        binding.chronometer.stop()
        viewModel.time = SystemClock.elapsedRealtime() - binding.chronometer.base
    }

    override fun onResume() {
        super.onResume()
        startChronometer()
    }

    override fun onPause() {
        super.onPause()
        stopChronometer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // TODO Save game state
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.boardLD.value?.clearObservers()
        _binding = null
    }
}