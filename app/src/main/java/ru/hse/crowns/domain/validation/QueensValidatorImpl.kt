package ru.hse.crowns.domain.validation

import ru.hse.crowns.domain.boards.QueensBoard
import kotlin.math.abs

class QueensValidatorImpl: QueensValidator {
    override fun check(board: QueensBoard): QueensMistake? {
        val queens: List<Pair<Int, Int>> = board.getQueenPositions()
        for (row in 0 until board.size) {
            val rowQueens = queens.filter { queen: Pair<Int, Int> -> queen.first == row }
            if (rowQueens.size > 1) {
                return QueensMistake.OneRow(*rowQueens.toTypedArray())
            }
        }

        for (column in 0 until board.size) {
            val columnQueens = queens.filter { queen: Pair<Int, Int> -> queen.second == column }
            if (columnQueens.size > 1) {
                return QueensMistake.OneColumn(*columnQueens.toTypedArray())
            }
        }

        for (polyominoId in 0 until board.getPolyominoCount()) {
            val polyominoQueens = queens.filter { queen: Pair<Int, Int> ->
                board.getPolyomino(queen.first, queen.second) == polyominoId
            }
            if (polyominoQueens.size > 1) {
                return QueensMistake.OnePolyomino(*polyominoQueens.toTypedArray())
            }
        }

        for(i in queens.indices){
            for(j in i + 1 until queens.size) {
                if (abs(queens[i].first - queens[j].first) <= 1 &&
                    abs(queens[i].second - queens[j].second) <= 1) {
                    return QueensMistake.Touch(queens[i], queens[j])
                }
            }
        }

        return null
    }
}