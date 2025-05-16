package ru.hse.crowns.domain.validation.queens

import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.QueensMistake
import kotlin.math.abs

class QueensValidatorImpl : QueensValidator {
    override fun check(board: QueensBoard): QueensMistake? {
        val queens: List<Pair<Int, Int>> = board.getQueenPositions()

        // Check for several queens in one row
        for (row in 0 until board.size) {
            val rowQueens = queens.filter { queen: Pair<Int, Int> -> queen.first == row }
            if (rowQueens.size > 1) {
                return QueensMistake.OneRow(rowQueens)
            }
        }

        // Check for several queens in one column
        for (column in 0 until board.size) {
            val columnQueens = queens.filter { queen: Pair<Int, Int> -> queen.second == column }
            if (columnQueens.size > 1) {
                return QueensMistake.OneColumn(columnQueens)
            }
        }

        // Check for several queens in one polyomino
        for (polyominoId in 0 until board.getPolyominoCount()) {
            val polyominoQueens = queens.filter { queen: Pair<Int, Int> ->
                board.getPolyomino(queen.first, queen.second) == polyominoId
            }
            if (polyominoQueens.size > 1) {
                return QueensMistake.OnePolyomino(polyominoQueens)
            }
        }

        // Check if there are queens touched each other
        for (i in queens.indices) {
            for (j in i + 1 until queens.size) {
                if (abs(queens[i].first - queens[j].first) <= 1 &&
                    abs(queens[i].second - queens[j].second) <= 1
                ) {
                    return QueensMistake.Touch(queens[i], queens[j])
                }
            }
        }

        // Check for empty row
        for (rowIndex in 0 until board.size) {
            if (board.getRow(rowIndex).all { status -> status == QueenCellStatus.CROSS }) {
                return QueensMistake.EmptyRow((0 until board.size).map { Pair(rowIndex, it) })
            }
        }

        // Check for empty column
        for (columnIndex in 0 until board.size) {
            if (board.getColumn(columnIndex).all { status -> status == QueenCellStatus.CROSS }) {
                return QueensMistake.EmptyColumn((0 until board.size).map {
                    Pair(it, columnIndex)
                })
            }
        }

        // Check for empty polyomino
        for (polyominoIndex in 0 until board.getPolyominoCount()) {
            if (board.getPolyominoCoordinates(polyominoIndex)
                .all { coordinates -> board.getStatus(coordinates.first, coordinates.second) == QueenCellStatus.CROSS }
            ) {
                return QueensMistake.EmptyPolyomino(board.getPolyominoCoordinates(polyominoIndex))
            }
        }

        return null
    }
}