package ru.hse.crowns.domain.validation

import ru.hse.crowns.domain.boards.NQueensBoard

class NQueensValidatorImpl: NQueensValidator {

    override fun check(board: NQueensBoard): NQueensMistake? {
        val queens: List<Pair<Int, Int>> = board.getQueenPositions()
        for (row in 0 until board.size) {
            val rowQueens = queens.filter { queen: Pair<Int, Int> -> queen.first == row }
            if (rowQueens.size > 1) {
                return NQueensMistake.OneRow(*rowQueens.toTypedArray())
            }
        }

        for (column in 0 until board.size) {
            val columnQueens = queens.filter { queen: Pair<Int, Int> -> queen.second == column }
            if (columnQueens.size > 1) {
                return NQueensMistake.OneColumn(*columnQueens.toTypedArray())
            }
        }

        for (diagonal in 0 until 2 * board.size - 1) {
            val upQueens =
                queens.filter { queen: Pair<Int, Int> -> queen.first + queen.second == diagonal }
            val downQueens =
                queens.filter { queen: Pair<Int, Int> -> queen.first - queen.second + board.size - 1 == diagonal }
            if(upQueens.size > 1) {
                return NQueensMistake.OneDiagonal(*upQueens.toTypedArray())
            }
            if(downQueens.size > 1) {
                return NQueensMistake.OneDiagonal(*downQueens.toTypedArray())
            }
        }

        return null
    }
}