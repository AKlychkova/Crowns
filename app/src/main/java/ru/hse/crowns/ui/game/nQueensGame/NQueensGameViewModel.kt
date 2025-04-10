package ru.hse.crowns.ui.game.nQueensGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hse.crowns.domain.boards.NQueensBoard
import ru.hse.crowns.domain.boards.QueenCellStatus
import ru.hse.crowns.domain.generation.Generator

class NQueensGameViewModel(
    private val boardGenerator: Generator<NQueensBoard>
) : ViewModel() {

    private val _boardLD = MutableLiveData<NQueensBoard>()
    val boardLD: LiveData<NQueensBoard> get() = _boardLD

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var generateJob: Job? = null

    fun generateBoard() {
        _isLoading.value = true
        generateJob?.cancel()
        generateJob = viewModelScope.launch(Dispatchers.Default) {
            val board = boardGenerator.generate()
            if (isActive) {
                launch(Dispatchers.Main) {
                    _boardLD.value = board
                    _isLoading.value = false
                }
            }
        }
    }

    fun updateBoard() {
        if (!boardLD.isInitialized) {
            generateBoard()
        }
    }

    fun onCellClick(row: Int, column: Int, eraseMode: Boolean, noteMode:Boolean) {
        val status = boardLD.value?.getStatus(row, column)
        if (status != QueenCellStatus.ORIGINAL_QUEEN) {
            if (eraseMode) {
                boardLD.value?.clearCell(row, column)
            } else if (noteMode && status != QueenCellStatus.CROSS) {
                boardLD.value?.setCross(row, column)
            } else if (!noteMode && status != QueenCellStatus.USER_QUEEN) {
                boardLD.value?.addUserQueen(row, column)
            } else {
                boardLD.value?.clearCell(row, column)
            }
        }
    }

}