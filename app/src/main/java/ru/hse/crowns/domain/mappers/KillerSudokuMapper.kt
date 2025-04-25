package ru.hse.crowns.domain.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.hse.crowns.data.repositories.KillerSudokuGameRepository
import ru.hse.crowns.domain.domainObjects.GameCharacteristics
import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.proto.KillerSudokuGameDTO
import ru.hse.crowns.utils.KillerSudokuDifficultyLevel
import kotlin.math.max

class KillerSudokuMapper(private val repository: KillerSudokuGameRepository) {
    val hasDataFlow: Flow<Boolean> = repository.gameFlow.map { it != null }

    private fun mapBoardToCells(board: KillerSudokuBoard): List<KillerSudokuGameDTO.Cell> {
        val list = ArrayList<KillerSudokuGameDTO.Cell>(board.size * board.size)
        for (i in 0 until board.size) {
            for (j in 0 until board.size) {
                list.add(
                    KillerSudokuGameDTO.Cell.newBuilder()
                        .setRow(i)
                        .setColumn(j)
                        .setValue(
                            if (board.isOriginal(i, j)) board.getValue(i, j)!!
                            else -(board.getValue(i, j) ?: 0)
                        )
                        .setPolyominoId(board.getPolyomino(i, j))
                        .addAllNotes(board.getNotes(i, j))
                        .build()
                )
            }
        }
        return list
    }

    private fun mapBoard(game: KillerSudokuGameDTO): KillerSudokuBoard {
        val cells = game.cellsList
        val size: Int =
            max(cells.maxOf { cell -> cell.row }, cells.maxOf { cell -> cell.column }) + 1
        val polyominoDivision = Array<IntArray>(size) { rowIndex ->
            IntArray(size) { columnIndex ->
                cells.first { cell -> cell.row == rowIndex && cell.column == columnIndex }.polyominoId
            }
        }
        val sudokuGrid = Array<IntArray>(size) { rowIndex ->
            IntArray(size) { columnIndex ->
                cells.first { cell -> cell.row == rowIndex && cell.column == columnIndex }.value
            }
        }
        val board = KillerSudokuBoard(
            sudokuGrid,
            polyominoDivision,
            game.polyominoSumsList.toIntArray()
        )
        for (cell in cells) {
            board.addAllNotes(cell.row, cell.column, cell.notesList)
        }
        return board
    }

    suspend fun saveGameData(board: KillerSudokuBoard,
                             time: Long, hintCount: Int,
                             mistakeCount: Int,
                             level : Int) {
        val game = KillerSudokuGameDTO.newBuilder()
            .setTime(time)
            .setHintCount(hintCount)
            .setMistakeCount(mistakeCount)
            .addAllCells(mapBoardToCells(board))
            .addAllPolyominoSums(
                (0 until board.getPolyominoCount()).map { id -> board.getSum(id) }
            )
            .setLevel(level)
            .build()
        withContext(Dispatchers.IO) {
            repository.updateData(game)
        }
    }

    suspend fun getBoard(): KillerSudokuBoard {
        return mapBoard(
            withContext(Dispatchers.IO) {repository.getData()!!}
        )
    }

    suspend fun getGameData(): GameCharacteristics {
        val game: KillerSudokuGameDTO = withContext(Dispatchers.IO) {repository.getData()!!}
        return GameCharacteristics(
            time = game.time,
            hintCount = game.hintCount,
            mistakeCount = game.mistakeCount
        )
    }

    suspend fun getLevel() : KillerSudokuDifficultyLevel {
        return KillerSudokuDifficultyLevel.entries[repository.getData()!!.level]
    }

    suspend fun removeData() {
        repository.removeData()
    }
}