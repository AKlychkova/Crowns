package ru.hse.crowns.ui.game.killerSudokuGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.crowns.data.repositories.BalanceRepository
import ru.hse.crowns.domain.prizeCalculation.PrizeCalculator
import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.Generator
import ru.hse.crowns.domain.hints.killerSudoku.KillerSudokuHint
import ru.hse.crowns.domain.hints.killerSudoku.KillerSudokuHintsProvider
import ru.hse.crowns.domain.mappers.KillerSudokuMapper
import ru.hse.crowns.domain.validation.gameStatuses.GameStatus
import ru.hse.crowns.domain.validation.gameStatuses.KillerSudokuMistake
import ru.hse.crowns.domain.validation.killerSudoku.KillerSudokuValidator
import ru.hse.crowns.utils.HINT_PRICE
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel

class KillerSudokuGameViewModel(
    private val boardGenerator: Generator<Int, KillerSudokuBoard>,
    private val boardValidator: KillerSudokuValidator,
    private val balanceRepository: BalanceRepository,
    private val gameDataMapper: KillerSudokuMapper,
    private val hintsProvider: KillerSudokuHintsProvider
) : ViewModel() {
    private val _boardLD = MutableLiveData<KillerSudokuBoard>()
    val boardLD: LiveData<KillerSudokuBoard> get() = _boardLD

    private val _isBoardLoading = MutableLiveData<Boolean>()
    val isBoardLoading: LiveData<Boolean> get() = _isBoardLoading

    private val _isMessageLoading = MutableLiveData<Boolean>(false)
    val isMessageLoading: LiveData<Boolean> get() = _isMessageLoading

    private val _status = MutableLiveData<GameStatus>()
    val status: LiveData<GameStatus> get() = _status

    private val _mistakeCounter = MutableLiveData<Int>(0)
    val mistakeCounter: LiveData<Int> get() = _mistakeCounter

    private val _hintCounter = MutableLiveData<Int>(0)
    val hintCounter: LiveData<Int> get() = _hintCounter

    private var _hint = MutableLiveData<KillerSudokuHint>()
    val hint: LiveData<KillerSudokuHint> = _hint

    val currentBalance = balanceRepository.coinsBalanceFlow.asLiveData()

    lateinit var level: KillerSudokuDifficultyLevel

    /**
     * Time in milliseconds
     */
    var time: Long = 0

    private var getBoardJob: Job? = null

    private fun generateBoard(level: KillerSudokuDifficultyLevel) {
        _isBoardLoading.value = true
        getBoardJob?.cancel()
        getBoardJob = viewModelScope.launch(Dispatchers.Default) {
            val board = boardGenerator.generate(level.getMaxToDelete())
            if (isActive) {
                launch(Dispatchers.Main) {
                    _boardLD.value = board
                    _isBoardLoading.value = false
                }
            }
        }
    }

    private fun readFromDataStore() {
        _isBoardLoading.value = true
        getBoardJob?.cancel()
        getBoardJob = viewModelScope.launch(Dispatchers.Default) {
            val board = gameDataMapper.getBoard()
            val data = gameDataMapper.getGameData()
            val lev = gameDataMapper.getLevel()
            if (isActive) {
                launch(Dispatchers.Main) {
                    _boardLD.value = board
                    _hintCounter.value = data.hintCount
                    _mistakeCounter.value = data.mistakeCount
                    _status.value = boardValidator.check(board) ?: GameStatus.Neutral
                    time = data.time
                    level = lev
                    _isBoardLoading.value = false
                }
            }
        }
    }

    /**
     * Initialize board, if it has not been initialized yet.
     * @param fromDataStore True, if board must be read from data store.
     * False, if it must be generated.
     * @param level difficulty level
     */
    fun updateBoard(fromDataStore: Boolean, level: KillerSudokuDifficultyLevel) {
        if (!boardLD.isInitialized) {
            if (fromDataStore) {
                readFromDataStore()
            } else {
                this.level = level
                generateBoard(level)
            }
        }
    }

    /**
     * On cell clicked callback
     * @param row row of clicked cell
     * @param column column of clicked cell
     * @param value value to set
     * @param eraseMode true if erase mode is active, else false
     * @param noteMode true if note mode is active, else false
     */
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

            // Check for mistakes
            val mistake: KillerSudokuMistake? = boardValidator.check(board)
            if (mistake != null) {
                _mistakeCounter.value = (_mistakeCounter.value ?: 0) + 1
                _status.value = mistake!!
            }
            // Check for winning
            else if (board.emptyCellsCount == 0) {
                _status.value = GameStatus.Win
                clearCache()
            } else {
                _status.value = GameStatus.Neutral
            }
        }
    }

    /**
     * Increase coins balance
     * @param prize the number of coins by which the balance will be increased
     */
    private fun increaseBalance(prize: Int) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.increaseCoinsBalance(prize)
    }

    private fun clearCache() = viewModelScope.launch(Dispatchers.IO) {
        gameDataMapper.removeData()
    }

    /**
     * @return number of coins won
     */
    fun calculatePrize(): Int {
        val prize = PrizeCalculator.calculate(
            time = (time / 60_000).toInt(),
            level = when (level) {
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

    /**
     * Generate new board, reset hint and mistake counters to zero, switch status to neutral
     */
    fun startNewGame() {
        generateBoard(level)
        _hintCounter.value = 0
        _mistakeCounter.value = 0
        _status.value = GameStatus.Neutral
    }

    /**
     * Save current game state
     */
    fun cache(level: Int) = viewModelScope.launch(Dispatchers.Default) {
        withContext(NonCancellable) {
            gameDataMapper.saveGameData(
                board = boardLD.value!!,
                time = time,
                hintCount = hintCounter.value!!,
                mistakeCount = mistakeCounter.value!!,
                level = level
            )
        }
    }

    /**
     * Try to provide a hint
     */
    fun getHint() {
        boardLD.value?.let { board ->
            _isMessageLoading.value = true
            viewModelScope.launch(Dispatchers.Default) {
                val hint = hintsProvider.provideHint(board)
                launch(Dispatchers.Main) {
                    if (hint !is KillerSudokuHint.Undefined) {
                        // Increase hint counter
                        _hintCounter.value = _hintCounter.value?.plus(1)
                        if (_hintCounter.value!! > 1) {
                            // If the hint is not first, decrease coins balance
                            balanceRepository.decreaseCoinsBalance(HINT_PRICE)
                        }
                    }
                    _hint.value = hint
                    _isMessageLoading.value = false
                }
            }
        }
    }

    /**
     * Rerun current level, reset hint and mistake counters to zero, switch status to neutral
     */
    fun rerun() {
        _isBoardLoading.value = true
        boardLD.value?.backToOriginal()
        _hintCounter.value = 0
        _mistakeCounter.value = 0
        _status.value = GameStatus.Neutral
        _isBoardLoading.value = false
    }
}