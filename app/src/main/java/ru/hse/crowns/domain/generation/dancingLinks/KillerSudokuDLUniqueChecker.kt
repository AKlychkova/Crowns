package ru.hse.crowns.domain.generation.dancingLinks

import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuUniqueChecker

class KillerSudokuDLUniqueChecker : DLUniqueChecker(), KillerSudokuUniqueChecker {
    private lateinit var board: KillerSudokuBoard

    /**
     * Sums of already filled polyomino elements
     */
    private lateinit var currentPolyominoSums: IntArray

    /**
     * Checks the correspondence of the polyomino sums
     */
    override val solutionPredicate: ((Array<BooleanArray>) -> Boolean) = { solution ->
        // Calculate polyomino sums in the solution
        val solutionPolyominoSums = currentPolyominoSums.clone()
        for (row: BooleanArray in solution) {
            for (i: Int in 0 until board.getPolyominoCount() * board.size) {
                if(row[4 * board.size * board.size + i]) {
                    solutionPolyominoSums[i / board.size] += i % board.size + 1
                    break
                }
            }
        }
        // Check if sums in solution equal to required sums
        var sumsAreEqual = true
        for(i:Int in 0 until board.getPolyominoCount()) {
            if(solutionPolyominoSums[i] != board.getSum(i)) {
                sumsAreEqual = false
                break
            }
        }
        sumsAreEqual
    }

    /**
     * @return column index in exact cover matrix assigned with the [cell]
     * @param cell number of cell
     */
    private fun getCellIndex(cell: Int) = cell

    /**
     * @return column index in exact cover matrix assigned with the [row] and the [value]
     */
    private fun getRowIndex(row: Int, value: Int) = board.size * board.size + row * board.size + (value - 1)

    /**
     * @return column index in exact cover matrix assigned with the [column][col] and the [value]
     */
    private fun getColumnIndex(col: Int, value: Int) = 2 * board.size * board.size + col * board.size + (value - 1)

    /**
     * @return column index in exact cover matrix assigned with the box and [value]
     */
    private fun getBoxIndex(row: Int, col: Int, value: Int) =
        3 * board.size * board.size + board.getBoxId(row, col) * board.size + (value - 1)

    /**
     * @return column index in exact cover matrix assigned with the polyomino and the [value]
     */
    private fun getPolyominoIndex(row: Int, col: Int, value: Int) =
        4 * board.size * board.size + board.getPolyomino(row, col) * board.size + (value - 1)

    /**
     * Create full exact cover matrix for Killer Sudoku puzzle (as if board will be empty)
     * @return 2D boolean array
     */
    private fun getKillerSudokuECMatrix(): Array<BooleanArray> {
        val ecMatrix = Array(board.size * board.size * board.size) {
            BooleanArray(4 * board.size * board.size + board.getPolyominoCount() * board.size) { false }
        }
        for (i: Int in 0 until board.size) {
            for (j: Int in 0 until board.size) {
                for (value: Int in board.values) {
                    val ecRow = (board.size * i + j) * board.size + value - 1
                    ecMatrix[ecRow][getCellIndex(board.size * i + j)] = true
                    ecMatrix[ecRow][getRowIndex(i, value)] = true
                    ecMatrix[ecRow][getColumnIndex(j, value)] = true
                    ecMatrix[ecRow][getBoxIndex(i, j, value)] = true
                    ecMatrix[ecRow][getPolyominoIndex(i, j, value)] = true
                }
            }
        }
        return ecMatrix
    }

    /**
     * Create dancing links matrix for Killer Sudoku puzzle defined by [board]
     * @return dancing links matrix
     */
    override fun createDLMatrix(): DLMatrix {
        val dlMatrix = DLMatrix(getKillerSudokuECMatrix(), 4 * board.size * board.size)
        val grid = board.getSudokuGrid()
        for (row in grid.indices) {
            for (col in grid.indices) {
                if (grid[row][col] in board.values) {
                    dlMatrix.coverColumn(getCellIndex(board.size * row + col))
                    dlMatrix.coverColumn(getRowIndex(row, grid[row][col]))
                    dlMatrix.coverColumn(getColumnIndex(col, grid[row][col]))
                    dlMatrix.coverColumn(getBoxIndex(row, col, grid[row][col]))
                    dlMatrix.coverColumn(getPolyominoIndex(row, col, grid[row][col]))
                }
            }
        }
        return dlMatrix
    }

    override suspend fun check(board: KillerSudokuBoard): Boolean {
        // Set board
        this.board = board
        currentPolyominoSums = IntArray(board.getPolyominoCount()) {0}

        // Calculate current polyomino sums
        for(i in 0 until board.size) {
            for(j in 0 until board.size) {
                currentPolyominoSums[board.getPolyomino(i, j)] += board.getValue(i, j) ?: 0
            }
        }

        // Run dancing links algorithm
        return check()
    }
}