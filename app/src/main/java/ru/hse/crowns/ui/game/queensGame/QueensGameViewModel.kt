package ru.hse.crowns.ui.game.queensGame

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
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.generation.Generator
import ru.hse.crowns.domain.hints.queens.QueensHint
import ru.hse.crowns.domain.hints.queens.QueensHintsProvider
import ru.hse.crowns.domain.mappers.QueensMapper
import ru.hse.crowns.domain.validation.gameStatuses.GameStatus
import ru.hse.crowns.domain.validation.gameStatuses.QueensMistake
import ru.hse.crowns.domain.validation.queens.QueensValidator
import ru.hse.crowns.utils.HINT_PRICE

class QueensGameViewModel(
    private val boardGenerator: Generator<Int, QueensBoard>,
    private val boardValidator: QueensValidator,
    private val balanceRepository: BalanceRepository,
    private val gameDataMapper: QueensMapper,
    private val hintsProvider: QueensHintsProvider
) : ViewModel() {

    private val _boardLD = MutableLiveData<QueensBoard>()
    val boardLD: LiveData<QueensBoard> get() = _boardLD

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

    private var _hint = MutableLiveData<QueensHint?>()
    val hint: LiveData<QueensHint?> = _hint

    val currentBalance = balanceRepository.coinsBalanceFlow.asLiveData()

    /**
     * Time in milliseconds
     */
    var time: Long = 0

    private var getBoardJob: Job? = null

    private fun generateBoard(boardSize: Int) {
        _isBoardLoading.value = true
        getBoardJob?.cancel()
        getBoardJob = viewModelScope.launch(Dispatchers.Default) {
            val board = boardGenerator.generate(boardSize)
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
            if (isActive) {
                launch(Dispatchers.Main) {
                    _boardLD.value = board
                    _hintCounter.value = data.hintCount
                    _mistakeCounter.value = data.mistakeCount
                    _status.value = boardValidator.check(board) ?: GameStatus.Neutral
                    time = data.time
                    _isBoardLoading.value = false
                }
            }
        }
    }

    /**
     * Initialize board, if it has not been initialized yet.
     * @param fromDataStore True, if board must be read from data store.
     * False, if it must be generated.
     * @param boardSize size of a board
     */
    fun updateBoard(fromDataStore: Boolean, boardSize: Int) {
        if (!boardLD.isInitialized) {
            if (fromDataStore) {
                readFromDataStore()
            } else {
                generateBoard(boardSize)
            }
        }
    }

    /**
     * On cell clicked callback
     * @param row row of clicked cell
     * @param column column of clicked cell
     * @param eraseMode true if erase mode is active, else false
     * @param noteMode true if note mode is active, else false
     */
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

            if (hint.value != null) {
                _hint.value = null
            }

            val mistake: QueensMistake? = boardValidator.check(board)
            if (mistake != null) {
                _mistakeCounter.value = (_mistakeCounter.value ?: 0) + 1
                _status.value = mistake!!
            } else if (board.getQueensCount() == board.size) {
                _status.value = GameStatus.Win
                clearCache()
            } else {
                _status.value = GameStatus.Neutral
            }
        }
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
            level = (5 - _boardLD.value!!.size).toDouble(),
            hintCount = _hintCounter.value ?: 0,
            mistakeCount = _mistakeCounter.value ?: 0
        )
        increaseBalance(prize)
        return prize
    }

    /**
     * Increase coins balance
     * @param prize the number of coins by which the balance will be increased
     */
    private fun increaseBalance(prize: Int) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.increaseCoinsBalance(prize)
    }

    /**
     * Generate new board and reset hint and mistake counters to zero
     */
    fun startNewGame() {
        _hintCounter.value = 0
        _mistakeCounter.value = 0
        _status.value = GameStatus.Neutral
        generateBoard(boardLD.value!!.size)
    }

    /**
     * Save current game state
     */
    fun cache() = viewModelScope.launch(Dispatchers.Default) {
        if (boardLD.value != null) {
            withContext(NonCancellable) {
                gameDataMapper.saveGameData(
                    board = boardLD.value!!,
                    time = time,
                    hintCount = hintCounter.value!!,
                    mistakeCount = mistakeCounter.value!!
                )
            }
        } else {
            getBoardJob?.cancel()
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
                    if (hint !is QueensHint.Undefined) {
                        _hintCounter.value = _hintCounter.value?.plus(1)
                        if (_hintCounter.value!! > 1) {
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