package ru.hse.crowns.ui.game.queensGame

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.hse.crowns.databinding.FragmentQueensGameBinding
import ru.hse.crowns.adapters.QueensBoardRecyclerAdapter
import ru.hse.crowns.domain.boards.BoardObserver
import ru.hse.crowns.domain.validation.GameStatus
import ru.hse.crowns.domain.validation.QueensMistake
import ru.hse.crowns.ui.dialogs.WinDialogFragment

class QueensGameFragment : Fragment() {
    private var _binding: FragmentQueensGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: QueensGameViewModel

    private lateinit var boardAdapter: QueensBoardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQueensGameBinding.inflate(inflater, container, false)

        // define viewmodel
        val boardSize: Int = arguments?.getInt("boardSize") ?: 8
        viewModel = getViewModel { parametersOf(boardSize) }

        // define recycler view
        binding.board.recyclerView.layoutManager = object : GridLayoutManager(context, boardSize) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        boardAdapter = QueensBoardRecyclerAdapter { row, column ->
            viewModel.onCellClick(
                row,
                column,
                binding.eraseToggleButton.isChecked,
                !binding.noteSwitch.isChecked
            )
        }
        binding.board.recyclerView.adapter = boardAdapter

        observeViewModel()

        return binding.root
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
            if(it is QueensMistake) {
                binding.mistakeMessageTextView.text = it.getMessage()
                boardAdapter.updateMistakes(it.queenPositions.toList())
            } else {
                binding.mistakeMessageTextView.text = ""
                boardAdapter.updateMistakes(emptyList())
            }
            if (it is GameStatus.Win) {
                binding.chronometer.stop()
                WinDialogFragment(viewModel.calculatePrize()
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
        viewModel.updateBoard(requireArguments().getBoolean("fromDataStore"))
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
        if(viewModel.status.value != GameStatus.Win) {
            viewModel.cache()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.boardLD.value?.clearObservers()
        _binding = null
    }
}