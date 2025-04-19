package ru.hse.crowns.domain.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.crowns.data.repositories.NQueensGameRepository
import ru.hse.crowns.domain.GameData
import ru.hse.crowns.domain.boards.NQueensBoard
import ru.hse.crowns.domain.boards.QueenCellStatus
import ru.hse.crowns.proto.NQueensGameDTO
import kotlin.math.max

class NQueensMapper(private val repository: NQueensGameRepository) {
    private fun mapDomainStatusToDataStatus(status: QueenCellStatus): NQueensGameDTO.CellStatus {
        return when (status) {
            QueenCellStatus.EMPTY -> NQueensGameDTO.CellStatus.EMPTY
            QueenCellStatus.ORIGINAL_QUEEN -> NQueensGameDTO.CellStatus.ORIGINAL_QUEEN
            QueenCellStatus.USER_QUEEN -> NQueensGameDTO.CellStatus.USER_QUEEN
            QueenCellStatus.CROSS -> NQueensGameDTO.CellStatus.CROSS
        }
    }

    private fun mapBoardToCells(board: NQueensBoard): List<NQueensGameDTO.Cell> {
        val list = ArrayList<NQueensGameDTO.Cell>(board.size * board.size)
        for (i in 0 until board.size) {
            for (j in 0 until board.size) {
                list.add(
                    NQueensGameDTO.Cell.newBuilder()
                        .setRow(i)
                        .setColumn(j)
                        .setStatus(mapDomainStatusToDataStatus(board.getStatus(i, j)))
                        .build()
                )
            }
        }
        return list
    }

    private fun mapCellsToBoard(cells: List<NQueensGameDTO.Cell>): NQueensBoard {
        val size: Int = max(cells.maxOf { cell -> cell.row }, cells.maxOf { cell -> cell.column }) + 1
        val board = NQueensBoard(size, emptyList())
        for (cell in cells) {
            when (cell.status) {
                NQueensGameDTO.CellStatus.ORIGINAL_QUEEN -> board.addQueen(cell.row, cell.column)
                NQueensGameDTO.CellStatus.USER_QUEEN -> board.addUserQueen(cell.row, cell.column)
                NQueensGameDTO.CellStatus.CROSS -> board.setCross(cell.row, cell.column)
                NQueensGameDTO.CellStatus.EMPTY,
                NQueensGameDTO.CellStatus.UNRECOGNIZED,
                null -> {
                }
            }
        }
        return board
    }

    suspend fun saveGameData(board: NQueensBoard, time: Long, hintCount: Int, mistakeCount: Int) {
        val game = NQueensGameDTO.newBuilder()
            .setTime(time)
            .setHintCount(hintCount)
            .setMistakeCount(mistakeCount)
            .addAllCells(mapBoardToCells(board))
            .build()
        withContext(Dispatchers.IO) {
            repository.updateData(game)
        }
    }

    suspend fun hasData(): Boolean {
        return repository.getData() != null
    }

    suspend fun getBoard(): NQueensBoard {
        return mapCellsToBoard(
            withContext(Dispatchers.IO) {
                repository.getData()!!.cellsList
            }
        )
    }

    suspend fun getGameData(): GameData {
        val game: NQueensGameDTO = withContext(Dispatchers.IO) { repository.getData()!! }
        return GameData(
            time = game.time,
            hintCount = game.hintCount,
            mistakeCount = game.mistakeCount
        )
    }

    suspend fun getBoardSize() : Int {
        val cells = withContext(Dispatchers.IO) { repository.getData()!!.cellsList }
        return max(cells.maxOf { cell -> cell.row }, cells.maxOf { cell -> cell.column }) + 1
    }

    suspend fun removeData() {
        repository.removeData()
    }
}