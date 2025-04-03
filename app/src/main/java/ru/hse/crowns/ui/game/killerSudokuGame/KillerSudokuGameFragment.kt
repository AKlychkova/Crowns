package ru.hse.crowns.ui.game.killerSudokuGame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ru.hse.crowns.databinding.FragmentKillerSudokuGameBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.hse.crowns.adapters.KillerSudokuBoardRecyclerAdapter
import ru.hse.crowns.domain.boards.BoardObserver

class KillerSudokuGameFragment() : Fragment() {

    private var _binding: FragmentKillerSudokuGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KillerSudokuGameViewModel by viewModel { parametersOf(50) }
    private lateinit var boardAdapter: KillerSudokuBoardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKillerSudokuGameBinding.inflate(inflater, container, false)
        observeViewModel()

        // define recycler view
        binding.boardRecyclerView.layoutManager = GridLayoutManager(context, 9)
        boardAdapter = KillerSudokuBoardRecyclerAdapter({ row, column ->
            viewModel.onCellClick(row, column)
        })
        binding.boardRecyclerView.adapter = boardAdapter

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        // TODO: Observe loading

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