package ru.hse.crowns.ui.game.queensGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hse.crowns.data.repositories.BalanceRepository
import ru.hse.crowns.domain.PrizeCalculator
import ru.hse.crowns.domain.boards.QueenCellStatus
import ru.hse.crowns.domain.boards.QueensBoard
import ru.hse.crowns.domain.generation.Generator
import ru.hse.crowns.domain.validation.GameStatus
import ru.hse.crowns.domain.validation.QueensMistake
import ru.hse.crowns.domain.validation.QueensValidator

class QueensGameViewModel(
    private val boardGenerator: Generator<QueensBoard>,
    private val boardValidator: QueensValidator,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val _boardLD = MutableLiveData<QueensBoard>()
    val boardLD: LiveData<QueensBoard> get() = _boardLD

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _status = MutableLiveData<GameStatus>()
    val status: LiveData<GameStatus> get() = _status

    private val _mistakeCounter = MutableLiveData<Int>(0)
    val mistakeCounter: LiveData<Int> get() = _mistakeCounter

    private val _hintCounter = MutableLiveData<Int>(0)
    val hintCounter: LiveData<Int> get() = _hintCounter

    /**
     * Time in milliseconds
     */
    var time: Long = 0

    private var generateJob: Job? = null

    private fun generateBoard() {
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

    fun onCellClick(row: Int, column: Int, eraseMode: Boolean, noteMode: Boolean) {
        boardLD.value?.let { board ->
            val status = board.getStatus(row, column)
            if (status != QueenCellStatus.ORIGINAL_QUEEN) {
                if (eraseMode) {
                    board.clearCell(row, column)
                } else if (noteMode && status != QueenCellStatus.CROSS) {
                    board.setCross(row, column)
                } else if (!noteMode && status != QueenCellStatus.USER_QUEEN) {
                    board.addUserQueen(row, column)
                } else {
                    board.clearCell(row, column)
                }
            }

            val mistake: QueensMistake? = boardValidator.check(board)
            if (mistake != null) {
                _mistakeCounter.value = (_mistakeCounter.value ?: 0) + 1
                _status.value = mistake!!
            } else if (board.getQueensCount() == board.size) {
                _status.value = GameStatus.Win
            } else {
                _status.value = GameStatus.Neutral
            }
        }
    }

    fun calculatePrize(): Int {
        val prize = PrizeCalculator.calculate(
            time = (time / 60_000).toInt(),
            level = (5 - _boardLD.value!!.size).toDouble(),
            hintCount = _hintCounter.value ?: 0,
            mistakeCount = _mistakeCounter.value ?: 0
        )
        increaseBalance(prize)
        return prize
    }

    private fun increaseBalance(prize:Int) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.increaseCoinsBalance(prize)
    }

    fun startNewGame() {
        _hintCounter.value = 0
        _mistakeCounter.value = 0
        _status.value = GameStatus.Neutral
        generateBoard()
    }
}