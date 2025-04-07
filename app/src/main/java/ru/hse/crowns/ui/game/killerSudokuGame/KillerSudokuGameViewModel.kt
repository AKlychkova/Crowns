package ru.hse.crowns.ui.game.killerSudokuGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.Generator

class KillerSudokuGameViewModel(
    private val boardGenerator: Generator<KillerSudokuBoard>
) : ViewModel() {
    private val _boardLD = MutableLiveData<KillerSudokuBoard>()
    val boardLD: LiveData<KillerSudokuBoard> get() = _boardLD

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var generateJob: Job? = null

    fun generateBoard() {
        _isLoading.value = true
        generateJob?.cancel()
        generateJob = viewModelScope.launch(Dispatchers.Default) {
            val board = boardGenerator.generate()
            if(isActive) {
                launch(Dispatchers.Main) {
                    _boardLD.value = board
                    _isLoading.value = false
                }
            }
        }
    }

    fun updateBoard() {
        if(!boardLD.isInitialized) {
            generateBoard()
        }
    }

    fun onCellClick(row: Int, column: Int) {
        // TODO
        boardLD.value?.clearCell(row, column)
    }
}