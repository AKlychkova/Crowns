package ru.hse.crowns.ui.game.killerSudokuGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.Generator

class KillerSudokuGameViewModel(
    private val boardGenerator: Generator<KillerSudokuBoard>
) : ViewModel() {
    private val _boardLD = MutableLiveData<KillerSudokuBoard>()
    val boardLD: LiveData<KillerSudokuBoard> get() = _boardLD

    private var generateJob: Job? = null

    fun generateBoard() {
        generateJob?.cancel()
        generateJob = viewModelScope.launch(Dispatchers.Default) {
            val board = boardGenerator.generate()
            launch(Dispatchers.Main) {
                _boardLD.value = board
            }
        }
    }

    fun updateBoard() {
        if(!boardLD.isInitialized) {
            generateBoard()
        }
    }

    fun onCellClick(row: Int, column: Int) {
        boardLD.value?.clearCell(row, column)
    }
}