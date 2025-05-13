package ru.hse.crowns.ui.game.nQueensGame

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.hse.crowns.R
import ru.hse.crowns.adapters.NQueensBoardRecyclerAdapter
import ru.hse.crowns.databinding.FragmentNQueensGameBinding
import ru.hse.crowns.domain.domainObjects.boards.BoardObserver
import ru.hse.crowns.domain.hints.nQueens.NQueensHint
import ru.hse.crowns.domain.validation.gameStatuses.GameStatus
import ru.hse.crowns.domain.validation.gameStatuses.NQueensMistake
import ru.hse.crowns.ui.dialogs.BuyHintDialog
import ru.hse.crowns.ui.dialogs.PauseDialogFragment
import ru.hse.crowns.ui.dialogs.WinDialogFragment
import ru.hse.crowns.utils.HINT_PRICE

class NQueensGameFragment : Fragment() {
    private var _binding: FragmentNQueensGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NQueensGameViewModel by viewModel()

    private lateinit var boardAdapter: NQueensBoardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNQueensGameBinding.inflate(inflater, container, false)

        boardAdapter = NQueensBoardRecyclerAdapter { row, column ->
            viewModel.onCellClick(
                row,
                column,
                binding.eraseToggleButton.isChecked,
                !binding.noteSwitch.isChecked
            )
        }
        binding.board.recyclerView.adapter = boardAdapter

        binding.hintImageButton.setOnClickListener {
            if (viewModel.status.value !is NQueensMistake) {
                if (viewModel.hintCounter.value!! > 0) {
                    BuyHintDialog { _, which: Int ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                viewModel.getHint()
                            }

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

        viewModel.hint.observe(viewLifecycleOwner) {
            when (it) {
                is NQueensHint.RowExclusionZone -> {
                    boardAdapter.updateHighlights(
                        greenPositions = it.zone,
                        redPositions = it.exclusion
                    )
                    binding.messageTextView.text =
                        getString(
                            R.string.n_queens_row_exclusion_zone_hint_text,
                            it.queensAmount.toString() +
                                    if (it.queensAmount == 1)
                                        " ферзь"
                                    else if (it.queensAmount < 5)
                                        " ферзя"
                                    else
                                        " ферзей"
                        )
                }

                is NQueensHint.ColumnExclusionZone -> {
                    boardAdapter.updateHighlights(
                        greenPositions = it.zone,
                        redPositions = it.exclusion
                    )
                    binding.messageTextView.text =
                        getString(
                            R.string.n_queens_column_exclusion_zone_hint_text,
                            it.queensAmount.toString() +
                                    if (it.queensAmount == 1)
                                        " ферзь"
                                    else if (it.queensAmount < 5)
                                        " ферзя"
                                    else
                                        " ферзей"
                        )
                }

                is NQueensHint.MissingCrosses -> {
                    boardAdapter.updateHighlights(greenPositions = it.positions)
                    binding.messageTextView.text =
                        getString(R.string.n_queens_missing_crosses_hint_text)
                }

                is NQueensHint.OneEmptyInColumn -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.empty),
                        redPositions = it.columnCells
                    )
                    binding.messageTextView.text =
                        getString(R.string.n_queens_one_in_column_hint_text)
                }

                is NQueensHint.OneEmptyInRow -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.empty),
                        redPositions = it.rowCells
                    )
                    binding.messageTextView.text =
                        getString(R.string.n_queens_one_in_row_hint_text)
                }

                is NQueensHint.RuleBreakingPlacement -> {
                    boardAdapter.updateHighlights(
                        greenPositions = listOf(it.position),
                        redPositions = it.ruleBreakingPositions
                    )
                    binding.messageTextView.text =
                        getString(R.string.n_queens_rule_breaking_placement_hint_text)
                }

                is NQueensHint.Undefined -> {
                    boardAdapter.updateHighlights()
                    binding.messageTextView.text = getString(R.string.undefined_hint_text)
                }
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
            if (it is NQueensMistake) {
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateBoard(
            requireArguments().getBoolean("fromDataStore"),
            requireArguments().getInt("boardSize")
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
        super.onStop()
        if (viewModel.status.value != GameStatus.Win) {
            viewModel.cache()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.boardLD.value?.clearObservers()
        _binding = null
    }
}