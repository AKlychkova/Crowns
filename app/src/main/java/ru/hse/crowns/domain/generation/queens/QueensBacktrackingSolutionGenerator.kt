package ru.hse.crowns.domain.generation.queens

import ru.hse.crowns.domain.boards.QueensBoard
import kotlin.math.abs

class QueensBacktrackingSolutionGenerator: QueensSolutionGenerator {
    /**
     * Generate a random solution to the Queens puzzle for an [n][boardSize]-by-[n][boardSize] board
     * using a simple backtracking algorithm
     * @param boardSize n
     * @return generated board
     */
    override fun generateSolution(boardSize: Int): QueensBoard {
        /**
         * An array in which each pair (index, value) corresponds to the coordinates of the queen (row, column)
         */
        val queenPositions = IntArray(boardSize) { -1 }

        /**
         * Each index corresponds to the column index of the board.
         * The value is true if there is no queen in this column, otherwise it is false.
         */
        val isColFree = BooleanArray(boardSize) { true }

        /**
         * The recursive function for placing queens on the board
         */
        fun placeQueen(row: Int): Boolean {
            // Base case: If all queens are placed
            if (row >= queenPositions.size) {
                return true
            }

            // Try placing the queen in different columns
            for (col in (0 until boardSize).shuffled()) {
                // Check if the queen can be placed in cell [row, col]
                if (isColFree[col] && (row == 0 || abs(queenPositions[row - 1] - col) > 1)) {
                    // Place this queen in cell [row,col]
                    queenPositions[row] = col
                    isColFree[col] = false
                    // Recur to place rest of the queens
                    if (placeQueen(row + 1)) {
                        return true
                    } else {
                        // Remove queen from cell [row, col]
                        isColFree[col] = true
                    }
                }
            }
            // If the queen cannot be placed in any row in this column
            return false
        }

        // Place queens recursively
        placeQueen(0)
        return QueensBoard(queenPositions)
    }
}