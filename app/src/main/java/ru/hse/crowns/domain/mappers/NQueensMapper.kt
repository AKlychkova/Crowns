package ru.hse.crowns.domain.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.hse.crowns.data.repositories.NQueensGameRepository
import ru.hse.crowns.domain.domainObjects.GameCharacteristics
import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.proto.NQueensGameDTO
import kotlin.math.max

class NQueensMapper(private val repository: NQueensGameRepository) {

    val hasDataFlow: Flow<Boolean> = repository.gameFlow.map { it != null }

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
        val size: Int =
            max(cells.maxOf { cell -> cell.row }, cells.maxOf { cell -> cell.column }) + 1
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

    suspend fun getBoard(): NQueensBoard {
        return mapCellsToBoard(
            withContext(Dispatchers.IO) {
                repository.getData()!!.cellsList
            }
        )
    }

    suspend fun getGameCharacteristic(): GameCharacteristics {
        val game: NQueensGameDTO = withContext(Dispatchers.IO) { repository.getData()!! }
        return GameCharacteristics(
            time = game.time,
            hintCount = game.hintCount,
            mistakeCount = game.mistakeCount
        )
    }

    suspend fun removeData() {
        repository.removeData()
    }
}