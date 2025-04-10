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

    fun onCellClick(row: Int, column: Int, value:Int, eraseMode:Boolean, noteMode: Boolean) {
        if (boardLD.value?.isOriginal(row, column) == false) {
            if (eraseMode) {
                boardLD.value?.clearCell(row, column)
            } else if (!noteMode) {
                if(boardLD.value?.getValue(row, column) != value) {
                    boardLD.value?.fillCellUser(row, column, value)
                } else {
                    boardLD.value?.clearCell(row, column)
                }
            } else {
                if(boardLD.value?.containNote(row, column, value) == true) {
                    boardLD.value?.removeNote(row, column, value)
                } else if (boardLD.value?.isEmpty(row, column) == true){
                    boardLD.value?.addNote(row, column, value)
                }
            }
        }
    }
}