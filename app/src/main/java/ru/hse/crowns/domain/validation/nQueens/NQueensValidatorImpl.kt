package ru.hse.crowns.domain.validation.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.validation.gameStatuses.NQueensMistake

class NQueensValidatorImpl : NQueensValidator {

    override fun check(board: NQueensBoard): NQueensMistake? {
        val queens: List<Pair<Int, Int>> = board.getQueenPositions()
        for (row in 0 until board.size) {
            val rowQueens = queens.filter { queen: Pair<Int, Int> -> queen.first == row }
            if (rowQueens.size > 1) {
                return NQueensMistake.OneRow(rowQueens)
            }
        }

        for (column in 0 until board.size) {
            val columnQueens = queens.filter { queen: Pair<Int, Int> -> queen.second == column }
            if (columnQueens.size > 1) {
                return NQueensMistake.OneColumn(columnQueens)
            }
        }

        for (diagonal in 0 until 2 * board.size - 1) {
            val upQueens =
                queens.filter { queen: Pair<Int, Int> -> queen.first + queen.second == diagonal }
            val downQueens =
                queens.filter { queen: Pair<Int, Int> -> queen.first - queen.second + board.size - 1 == diagonal }
            if (upQueens.size > 1) {
                return NQueensMistake.OneDiagonal(upQueens)
            }
            if (downQueens.size > 1) {
                return NQueensMistake.OneDiagonal(downQueens)
            }
        }

        for (rowIndex in 0 until board.size) {
            if (board.getRow(rowIndex).all { status -> status == QueenCellStatus.CROSS }) {
                return NQueensMistake.EmptyRow((0 until board.size).map { Pair(rowIndex, it) })
            }
        }

        for (columnIndex in 0 until board.size) {
            if (board.getColumn(columnIndex).all { status -> status == QueenCellStatus.CROSS }) {
                return NQueensMistake.EmptyColumn((0 until board.size).map {
                    Pair(it, columnIndex)
                })
            }
        }

        return null
    }
}