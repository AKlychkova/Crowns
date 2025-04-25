package ru.hse.crowns.domain.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.hse.crowns.data.repositories.QueensGameRepository
import ru.hse.crowns.domain.domainObjects.GameCharacteristics
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.proto.QueensGameDTO
import kotlin.math.max

class QueensMapper(private val repository: QueensGameRepository) {
    val hasDataFlow: Flow<Boolean> = repository.gameFlow.map { it != null }

    private fun mapDomainStatusToDataStatus(status: QueenCellStatus): QueensGameDTO.CellStatus {
        return when (status) {
            QueenCellStatus.EMPTY -> QueensGameDTO.CellStatus.EMPTY
            QueenCellStatus.ORIGINAL_QUEEN -> QueensGameDTO.CellStatus.ORIGINAL_QUEEN
            QueenCellStatus.USER_QUEEN -> QueensGameDTO.CellStatus.USER_QUEEN
            QueenCellStatus.CROSS -> QueensGameDTO.CellStatus.CROSS
        }
    }

    private fun mapBoardToCells(board: QueensBoard): List<QueensGameDTO.Cell> {
        val list = ArrayList<QueensGameDTO.Cell>(board.size * board.size)
        for (i in 0 until board.size) {
            for (j in 0 until board.size) {
                list.add(
                    QueensGameDTO.Cell.newBuilder()
                        .setRow(i)
                        .setColumn(j)
                        .setStatus(mapDomainStatusToDataStatus(board.getStatus(i, j)))
                        .setPolyominoId(board.getPolyomino(i, j))
                        .build()
                )
            }
        }
        return list
    }

    private fun mapCellsToBoard(cells: List<QueensGameDTO.Cell>): QueensBoard {
        val size: Int =
            max(cells.maxOf { cell -> cell.row }, cells.maxOf { cell -> cell.column }) + 1
        val polyominoDivision = Array<IntArray>(size) { rowIndex ->
            IntArray(size) { columnIndex ->
                cells.first { cell -> cell.row == rowIndex && cell.column == columnIndex }.polyominoId
            }
        }
        val board = QueensBoard(size, emptyList(), polyominoDivision)
        for (cell in cells) {
            when (cell.status) {
                QueensGameDTO.CellStatus.ORIGINAL_QUEEN -> board.addQueen(cell.row, cell.column)
                QueensGameDTO.CellStatus.USER_QUEEN -> board.addUserQueen(cell.row, cell.column)
                QueensGameDTO.CellStatus.CROSS -> board.setCross(cell.row, cell.column)
                QueensGameDTO.CellStatus.EMPTY,
                QueensGameDTO.CellStatus.UNRECOGNIZED,
                null -> {
                }
            }
        }
        return board
    }

    suspend fun saveGameData(board: QueensBoard, time: Long, hintCount: Int, mistakeCount: Int) {
        val game = QueensGameDTO.newBuilder()
            .setTime(time)
            .setHintCount(hintCount)
            .setMistakeCount(mistakeCount)
            .addAllCells(mapBoardToCells(board))
            .build()
        withContext(Dispatchers.IO) {
            repository.updateData(game)
        }
    }

    suspend fun getBoard(): QueensBoard {
        return mapCellsToBoard(
            withContext(Dispatchers.IO) {
                repository.getData()!!.cellsList
            }
        )
    }

    suspend fun getGameData(): GameCharacteristics {
        val game: QueensGameDTO = withContext(Dispatchers.IO) { repository.getData()!! }
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