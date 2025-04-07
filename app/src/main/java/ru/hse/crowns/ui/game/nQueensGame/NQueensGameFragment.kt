package ru.hse.crowns.ui.game.nQueensGame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.hse.crowns.adapters.NQueensBoardRecyclerAdapter
import ru.hse.crowns.databinding.FragmentNQueensGameBinding
import ru.hse.crowns.domain.boards.BoardObserver

class NQueensGameFragment : Fragment() {
    private var _binding: FragmentNQueensGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NQueensGameViewModel

    private lateinit var boardAdapter: NQueensBoardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNQueensGameBinding.inflate(inflater, container, false)

        // define viewmodel
        val boardSize: Int = arguments?.getInt("boardSize") ?: 8
        viewModel = getViewModel { parametersOf(boardSize) }

        // define recycler view
        binding.board.recyclerView.layoutManager = object : GridLayoutManager(context, boardSize) {
            override fun canScrollVertically() : Boolean {
                return false
            }
        }

        boardAdapter = NQueensBoardRecyclerAdapter { row, column ->
            viewModel.onCellClick(row, column)
        }
        binding.board.recyclerView.adapter = boardAdapter

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if(it) {
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