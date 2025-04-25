package ru.hse.crowns.domain.hints.killerSudoku

import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.QueensMistake
import kotlin.math.max
import kotlin.math.min

class KillerSudokuHintsProviderImpl() : KillerSudokuHintsProvider {

    /**
     * @return position of the cell if it is the only one empty cell in the row
     */
    private fun findOneEmptyInRow(board: KillerSudokuBoard): Pair<Int, Int>? {
        for (rowId in 0 until board.size) {
            if (board.getRow(rowId).count { it == null } == 1) {
                val colId = board.getRow(rowId).indexOfFirst { it == null }
                return Pair(rowId, colId)
            }
        }
        return null
    }

    /**
     * @return position of the cell if it is the only one empty cell in the column
     */
    private fun findOneEmptyInColumn(board: KillerSudokuBoard): Pair<Int, Int>? {
        for (colId in 0 until board.size) {
            if (board.getColumn(colId).count { it == null } == 1) {
                val rowId = board.getColumn(colId).indexOfFirst { it == null }
                return Pair(rowId, colId)
            }
        }
        return null
    }

    /**
     * @return position of the cell if it is the only one empty cell in the polyomino
     */
    private fun findOneEmptyInPolyomino(board: KillerSudokuBoard): Pair<Int, Int>? {
        for (polyominoId in 0 until board.getPolyominoCount()) {
            if (board.getPolyominoCoordinates(polyominoId)
                    .count { board.getValue(it.first, it.second) == null } == 1
            ) {
                val position = board.getPolyominoCoordinates(polyominoId)
                    .first { pos ->
                        board.getValue(pos.first, pos.second) == null
                    }
                return position
            }
        }
        return null
    }

    /**
     * @return position of the cell if it is the only one empty cell in the box
     */
    private fun findOneEmptyInBox(board: KillerSudokuBoard): Pair<Int, Int>? {
        for (boxId in 0 until board.boxesInRow * board.boxesInRow) {
            if (board.getBox(boxId).count { it == null } == 1) {
                val position = board.getBoxCoordinates(boxId)
                    .first { pos ->
                        board.getValue(pos.first, pos.second) == null
                    }
                return position
            }
        }
        return null
    }

    override fun provideHint(board: KillerSudokuBoard): KillerSudokuHint {
        findOneEmptyInRow(board)?.let { empty ->
            val row = (0 until board.size)
                .filterNot { it == empty.second }
                .map { Pair(empty.first, it) }
            val value: Int = board.values.first{value -> value !in board.getRow(empty.first)}
            return KillerSudokuHint.OneEmptyInRow(empty, row, value)
        }
        findOneEmptyInColumn(board)?.let { empty ->
            val col = (0 until board.size)
                .filterNot { it == empty.first }
                .map { Pair(it, empty.second) }
            val value: Int = board.values.first{value -> value !in board.getColumn(empty.second)}
            return KillerSudokuHint.OneEmptyInColumn(empty, col, value)
        }
        findOneEmptyInPolyomino(board)?.let { empty ->
            val pId = board.getPolyomino(empty.first, empty.second)
            val polyomino = board.getPolyominoCoordinates(pId)
            val value: Int = board.getSum(pId) - polyomino.sumOf {pos -> board.getValue(pos.first, pos.second) ?: 0 }
            return KillerSudokuHint.OneEmptyInPolyomino(empty, polyomino, value)
        }
        findOneEmptyInBox(board)?.let{ empty ->
            val bId = board.getBoxId(empty.first, empty.second)
            val box = board.getBoxCoordinates(bId).toList()
            val value = board.values.first{value -> value !in board.getBox(bId)}
            return KillerSudokuHint.OneEmptyInBox(empty, box, value)
        }

        return KillerSudokuHint.Undefined
    }


}