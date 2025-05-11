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
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.databinding.FragmentKillerSudokuGameBinding
import ru.hse.crowns.R
import ru.hse.crowns.adapters.KillerSudokuBoardRecyclerAdapter
import ru.hse.crowns.domain.domainObjects.boards.BoardObserver
import ru.hse.crowns.domain.hints.killerSudoku.KillerSudokuHint
import ru.hse.crowns.domain.validation.gameStatuses.GameStatus
import ru.hse.crowns.domain.validation.gameStatuses.KillerSudokuMistake
import ru.hse.crowns.ui.dialogs.BuyHintDialog
import ru.hse.crowns.ui.dialogs.PauseDialogFragment
import ru.hse.crowns.ui.dialogs.WinDialogFragment
import ru.hse.crowns.utils.HINT_PRICE
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel

class KillerSudokuGameFragment : Fragment() {
    private var _binding: FragmentKillerSudokuGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KillerSudokuGameViewModel by viewModel()
    private lateinit var boardAdapter: KillerSudokuBoardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKillerSudokuGameBinding.inflate(inflater, container, false)

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

        binding.hintImageButton.setOnClickListener {
            if (viewModel.status.value !is KillerSudokuMistake) {
                if (viewModel.hintCounter.value!! > 0) {
                    BuyHintDialog { _, which: Int ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> viewModel.getHint()
                            DialogInterface.BUTTON_NEUTRAL -> {}
                        }
                    }.show(childFragmentManager, "BuyDialog")
                } else {
                    viewModel.getHint()
                }
            }
        }

        binding.pauseImageButton.setOnClickListener {
            stopChronometer()
            PauseDialogFragment(
                onClickListener = { _, which: Int ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            viewModel.startNewGame()
                            viewModel.time = 0
                            startChronometer()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            viewModel.rerun()
                            viewModel.time = 0
                            startChronometer()
                        }

                        DialogInterface.BUTTON_NEUTRAL -> findNavController().popBackStack()
                    }
                },
                onCancel = {
                    startChronometer()
                }
            ).show(childFragmentManager, "PauseDialog")
        }

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
        viewModel.isBoardLoading.observe(viewLifecycleOwner) {
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

        viewModel.isMessageLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.messageProgressBar.visibility = View.VISIBLE
                binding.messageTextView.visibility = View.INVISIBLE
            } else {
                binding.messageProgressBar.visibility = View.INVISIBLE
                binding.messageTextView.visibility = View.VISIBLE
            }
        }

        viewModel.boardLD.observe(viewLifecycleOwner) {
            binding.board.recyclerView.layoutManager =
                object : GridLayoutManager(context, it.size) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            it.addObserver(object : BoardObserver {
                override fun onChanged(row: Int, column: Int) {
                    boardAdapter.updateCellValue(row, column)
                }
            })
            boardAdapter.setBoard(it)
        }

        viewModel.status.observe(viewLifecycleOwner) {
            if (it is KillerSudokuMistake) {
                binding.messageTextView.text = it.getMessage()
                boardAdapter.updateHighlights(redPositions = it.positions.toList())
            } else {
                binding.messageTextView.text = ""
                boardAdapter.updateHighlights()
            }
            if (it is GameStatus.Win) {
                stopChronometer()
                WinDialogFragment(
                    viewModel.calculatePrize()
                ) { _, which: Int ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            viewModel.startNewGame()
                            viewModel.time = 0
                            startChronometer()
                        }

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
            if (it > 0 && viewModel.currentBalance.value!! < HINT_PRICE) {
                binding.hintImageButton.isEnabled = false
            }
        }

        viewModel.currentBalance.observe(viewLifecycleOwner) {
            if (it < HINT_PRICE && viewModel.hintCounter.value!! > 0) {
                binding.hintImageButton.isEnabled = false
            } else {
                binding.hintImageButton.isEnabled = true
            }
        }

        viewModel.hint.observe(viewLifecycleOwner) {
            when (it) {
                is KillerSudokuHint.OneEmptyInColumn -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.empty),
                        redPositions = it.columnCells
                    )
                    binding.messageTextView.text =
                        getString(R.string.ksudoku_one_in_column_hint_text, it.value)
                }

                is KillerSudokuHint.OneEmptyInPolyomino -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.empty),
                        redPositions = it.polyominoCells
                    )
                    binding.messageTextView.text =
                        getString(R.string.ksudoku_one_in_polyomino_hint_text, it.value)
                }

                is KillerSudokuHint.OneEmptyInRow -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.empty),
                        redPositions = it.rowCells
                    )
                    binding.messageTextView.text =
                        getString(R.string.ksudoku_one_in_row_hint_text, it.value)
                }

                KillerSudokuHint.Undefined -> {
                    boardAdapter.updateHighlights()
                    binding.messageTextView.text = getString(R.string.undefined_hint_text)
                }

                is KillerSudokuHint.OneEmptyInBox -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.empty),
                        redPositions = it.boxCells
                    )
                    binding.messageTextView.text =
                        getString(R.string.ksudoku_one_in_box_hint_text, it.value)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateBoard(
            requireArguments().getBoolean("fromDataStore"),
            KillerSudokuDifficultyLevel.entries[requireArguments().getInt("difficultyLevel")]
        )
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

    override fun onStop() {
        if (viewModel.status.value != GameStatus.Win) {
            viewModel.cache(requireArguments().getInt("difficultyLevel"))
        }
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.boardLD.value?.clearObservers()
        _binding = null
    }
}