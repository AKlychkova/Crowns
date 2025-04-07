package ru.hse.crowns.domain.generation.dancingLinks

import ru.hse.crowns.domain.boards.NQueensBoard
import ru.hse.crowns.domain.generation.nQueens.NQueensUniqueChecker

class NQueensDLUniqueChecker : DLUniqueChecker(), NQueensUniqueChecker {
    override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null
    private lateinit var board: NQueensBoard

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
     * @return column index in exact cover matrix assigned with the down-up diagonal of the cell
     * @param row row of the cell
     * @param col column of the cell
     */
    private fun getUpIndex(row: Int, col: Int) = 2 * board.size + row + col

    /**
     * @return column index in exact cover matrix assigned with the up-down diagonal of the cell
     * @param row row of the cell
     * @param col column of the cell
     */
    private fun getDownIndex(row: Int, col: Int) = 5 * board.size + row - col - 2

    /**
     * Create full exact cover matrix for N Queens puzzle (as if board will be empty)
     * @return 2D boolean array
     */
    private fun getNQueensECMatrix(): Array<BooleanArray> {
        val ecMatrix = Array(board.size * board.size) { BooleanArray(6 * board.size - 2) { false } }
        for (i: Int in 0 until board.size) {
            for (j: Int in 0 until board.size) {
                ecMatrix[board.size * i + j][getRowIndex(i)] = true
                ecMatrix[board.size * i + j][getColumnIndex(j)] = true
                ecMatrix[board.size * i + j][getUpIndex(i, j)] = true
                ecMatrix[board.size * i + j][getDownIndex(i, j)] = true
            }
        }
        return ecMatrix
    }

    /**
     * Create dancing links matrix for N Queens puzzle defined by [board]
     * @return dancing links matrix
     */
    override fun createDLMatrix(): DLMatrix {
        val dlMatrix = DLMatrix(getNQueensECMatrix(), 2 * board.size)
        val positions = board.getQueenPositions()
        for (queen: Pair<Int, Int> in positions) {
            dlMatrix.coverColumn(getRowIndex(queen.first))
            dlMatrix.coverColumn(getColumnIndex(queen.second))
            dlMatrix.coverColumn(getUpIndex(queen.first, queen.second))
            dlMatrix.coverColumn(getDownIndex(queen.first, queen.second))
        }
        return dlMatrix
    }

    override suspend fun check(board: NQueensBoard): Boolean {
        this.board = board
        return check()
    }
}