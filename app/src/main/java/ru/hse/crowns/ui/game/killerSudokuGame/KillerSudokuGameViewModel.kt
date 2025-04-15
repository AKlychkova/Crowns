package ru.hse.crowns.ui.game.killerSudokuGame

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
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.Generator
import ru.hse.crowns.domain.validation.GameStatus
import ru.hse.crowns.domain.validation.KillerSudokuMistake
import ru.hse.crowns.domain.validation.KillerSudokuValidator
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel

class KillerSudokuGameViewModel(
    private val boardGenerator: Generator<KillerSudokuBoard>,
    private val boardValidator: KillerSudokuValidator,
    private val balanceRepository: BalanceRepository
) : ViewModel() {
    private val _boardLD = MutableLiveData<KillerSudokuBoard>()
    val boardLD: LiveData<KillerSudokuBoard> get() = _boardLD

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

    fun onCellClick(row: Int, column: Int, value: Int, eraseMode: Boolean, noteMode: Boolean) {
        boardLD.value?.let { board ->
            if (!board.isOriginal(row, column)) {
                if (eraseMode) {
                    board.clearCell(row, column)
                } else if (!noteMode) {
                    if (board.getValue(row, column) != value) {
                        board.fillCellUser(row, column, value)
                    } else {
                        board.clearCell(row, column)
                    }
                } else {
                    if (board.containNote(row, column, value)) {
                        board.removeNote(row, column, value)
                    } else if (board.isEmpty(row, column)) {
                        board.addNote(row, column, value)
                    }
                }
            }

            val mistake: KillerSudokuMistake? = boardValidator.check(board)
            if (mistake != null) {
                _mistakeCounter.value = (_mistakeCounter.value ?: 0) + 1
                _status.value = mistake!!
            } else if (board.emptyCellsCount == 0) {
                _status.value = GameStatus.Win
            } else {
                _status.value = GameStatus.Neutral
            }
        }
    }

    private fun increaseBalance(prize:Int) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.increaseCoinsBalance(prize)
    }

    fun calculatePrize(level: KillerSudokuDifficultyLevel): Int {
        val prize = PrizeCalculator.calculate(
            time = (time / 60_000).toInt(),
            level = when(level) {
                KillerSudokuDifficultyLevel.EASY -> 0.0
                KillerSudokuDifficultyLevel.MEDIUM -> 2.5
                KillerSudokuDifficultyLevel.DIFFICULT -> 5.0
            },
            hintCount = _hintCounter.value ?: 0,
            mistakeCount = _mistakeCounter.value ?: 0
        )
        increaseBalance(prize)
        return prize
    }

    fun startNewGame() {
        generateBoard()
        _hintCounter.value = 0
        _mistakeCounter.value = 0
        _status.value = GameStatus.Neutral
    }
}