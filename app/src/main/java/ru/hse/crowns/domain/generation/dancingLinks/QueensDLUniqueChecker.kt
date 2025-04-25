package ru.hse.crowns.domain.generation.dancingLinks

import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.generation.queens.QueensUniqueChecker

class QueensDLUniqueChecker : DLUniqueChecker(), QueensUniqueChecker {
    private lateinit var board: QueensBoard
    override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

    /**
     * @return column index in exact cover matrix assigned with the [row] of the game board
     * @param row row of the game board
     */
    private fun getRowIndex(row: Int) = row

    /**
     * @return column index in exact cover matrix assigned with the [column][col] of the game board
     * @param col column of the game board
     */
    private fun getColumnIndex(col: Int) = board.size + col

    /**
     * @return column index in exact cover matrix assigned with the polyomino of the cell
     * @param row row of the cell
     * @param col column of the cell
     */
    private fun getPolyominoIndex(row: Int, col: Int) = 2 * board.size + board.getPolyomino(row, col)

    /**
     * @return indexes of 2x2 squares (aka boxes) in which the cell falls
     * @param row row of the cell
     * @param col column of the cell
     */
    private fun getBoxesIndices(row: Int, col: Int) = sequence {
        val offsets = listOf(Pair(-1, -1), Pair(-1, 0), Pair(0, -1), Pair(0, 0))
        for (offset in offsets) {
            if (row + offset.first in 0 until board.size - 1 &&
                col + offset.second in 0 until board.size - 1
            ) {
                val boxNumber: Int = (row + offset.first) * (board.size - 1) + (col + offset.second)
                yield(3 * board.size + boxNumber)
            }
        }
    }

    /**
     * Create full exact cover matrix for Queens puzzle (as if board will be empty)
     * @return 2D boolean array
     */
    private fun getQueensECMatrix(): Array<BooleanArray> {
        val ecMatrix =
            Array(board.size * board.size) { BooleanArray(board.size * board.size + board.size + 1) { false } }
        for (i: Int in 0 until board.size) {
            for (j: Int in 0 until board.size) {
                ecMatrix[board.size * i + j][getRowIndex(i)] = true
                ecMatrix[board.size * i + j][getColumnIndex(j)] = true
                ecMatrix[board.size * i + j][getPolyominoIndex(i, j)] = true
                getBoxesIndices(i, j).forEach { index: Int ->
                    ecMatrix[board.size * i + j][index] = true
                }
            }
        }
        return ecMatrix
    }

    /**
     * Create dancing links matrix for N Queens puzzle defined by [board]
     * @return dancing links matrix
     */
    override fun createDLMatrix(): DLMatrix {
        val dlMatrix = DLMatrix(
            getQueensECMatrix(),
            3 * board.size
        )
        val positions = board.getQueenPositions()
        for (queen: Pair<Int, Int> in positions) {
            dlMatrix.coverColumn(getRowIndex(queen.first))
            dlMatrix.coverColumn(getColumnIndex(queen.second))
            dlMatrix.coverColumn(getPolyominoIndex(queen.first, queen.second))
            getBoxesIndices(queen.first, queen.second).forEach { index: Int ->
                dlMatrix.coverColumn(index)
            }
        }
        return dlMatrix
    }

    override suspend fun check(board: QueensBoard): Boolean {
        this.board = board
        return check()
    }
}