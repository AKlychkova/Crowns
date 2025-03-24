package ru.hse.crowns.generation.killerSudoku

import kotlin.math.roundToInt
import kotlin.math.sqrt

class KillerSudokuBacktrackingSolutionGenerator : KillerSudokuSolutionGenerator {
    private val boardSize: Int = 9
    private val boxSize: Int = sqrt(boardSize.toDouble()).roundToInt()
    private val values = 1..boardSize
    private val boxesInRow = boardSize / boxSize

    private val grid = Array<IntArray>(boardSize) { IntArray(boardSize) { 0 } }

    /**
     * @return box id by cell coordinates
     */
    private fun getBoxId(row: Int, col: Int): Int = row / boxSize * boxesInRow + col / boxSize

    /**
     * Randomly fill the diagonal boxes
     */
    private fun fillDiagonal() {
        for (boxRow in 0 until boxesInRow) {
            val boxValues = values.shuffled()
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    grid[boxRow * boxSize + i][boxRow * boxSize + j] = boxValues[i * boxSize + j]
                }
            }
        }
    }

    /**
     * Check if it is safe to put [value] in [row]
     */
    private fun isUnusedInRow(row: Int, value: Int): Boolean {
        for (col in 0 until boardSize) {
            if (grid[row][col] == value) {
                return false
            }
        }
        return true
    }

    /**
     * Check if it is safe to put [value] in [column]
     */
    private fun isUnusedInCol(column: Int, value: Int): Boolean {
        for (row in 0 until boardSize) {
            if (grid[row][column] == value) {
                return false
            }
        }
        return true
    }

    /**
     * @return false if box with [boxId] contains [value], otherwise true
     */
    private fun isUnusedInBox(boxId: Int, value: Int): Boolean {
        val rowStart: Int = boxId / boxesInRow * boxSize
        val colStart: Int = boxId % boxesInRow * boxSize
        for (i in 0 until boxSize) {
            for (j in 0 until boxSize) {
                if (grid[rowStart + i][colStart + j] == value) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Check if it is safe to put [value] in cell with coordinates ([row], [col])
     */
    private fun checkIfSafe(row: Int, col: Int, value: Int): Boolean {
        return (isUnusedInRow(row, value) &&
                isUnusedInCol(col, value) &&
                isUnusedInBox(getBoxId(row, col), value))
    }

    /**
     * Recursively fill cells in not diagonal boxes
     */
    private fun fillNotDiagonal(cell: Int): Boolean {
        // Base case: if all cells are filled
        if (cell >= boardSize * boardSize) {
            return true
        }

        val row = cell / boardSize
        val col = cell % boardSize

        // Try putting different values in the cell
        for (value in values.shuffled()) {
            // Check if the value can be placed in the cell
            if (checkIfSafe(row, col, value)) {
                // Put the value to the cell
                grid[row][col] = value
                // Recur to fill rest of the cells
                if (fillNotDiagonal(getNextEmptyCell(cell))) {
                    return true
                }
                // Clear cell
                grid[row][col] = 0
            }
        }
        return false
    }

    /**
     * @return next cell which is not in a diagonal box
     */
    private fun getNextEmptyCell(currentCell: Int): Int {
        val nextCell = currentCell + 1
        val boxNum = getBoxId(nextCell / boardSize, nextCell % boardSize)
        return if (boxNum / boxesInRow != boxNum % boxesInRow) {
            nextCell
        } else {
            nextCell + (boxSize - nextCell % boxSize)
        }
    }

    /**
     * Generate a random valid solution to Killer Sudoku puzzle using a simple backtracking algorithm
     * @return generated board
     */
    override fun generateSolution(): KillerSudokuBoard {
        fillDiagonal()
        fillNotDiagonal(getNextEmptyCell(0))
        return KillerSudokuBoard(grid)
    }
}