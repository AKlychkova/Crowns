package ru.hse.crowns.generation.nQueens

import ru.hse.crowns.boards.NQueensBoard

class NQueensBacktrackingSolutionGenerator : NQueensSolutionGenerator {
    /**
     * Generate a random solution to the N queens problem for an [n][boardSize]-by-[n][boardSize] board
     * using a simple backtracking algorithm
     * @param boardSize n
     * @return generated board
     */
    override fun generateSolution(boardSize: Int): NQueensBoard {
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
         * Each index corresponds to the down-up diagonal of the board.
         * The index is equal to the sum of the coordinates of the diagonal element.
         * The value is true if there is no queen in this diagonal, otherwise it is false.
         */
        val isUpFree = BooleanArray(2 * boardSize - 1) { true }

        /**
         * Each index corresponds to the up-down diagonal of the board.
         * The index is equal to row - column + [boardSize] - 1,
         * where row and column are coordinates of diagonal element.
         * The value is true if there is no queen in this diagonal, otherwise it is false.
         */
        val isDownFree = BooleanArray(2 * boardSize - 1) { true }

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
                if (isColFree[col] && isUpFree[row + col] && isDownFree[row - col + boardSize - 1]) {
                    // Place this queen in cell [row,col]
                    queenPositions[row] = col
                    isColFree[col] = false
                    isUpFree[row + col] = false
                    isDownFree[row - col + boardSize - 1] = false

                    // Recur to place rest of the queens
                    if (placeQueen(row + 1)) {
                        return true
                    } else {
                        // Remove queen from cell [row, col]
                        isColFree[col] = true
                        isUpFree[row + col] = true
                        isDownFree[row - col + boardSize - 1] = true
                    }
                }
            }
            // If the queen can not be placed in any row in this column
            return false
        }

        // Place queens recursively
        placeQueen(0)
        return NQueensBoard(queenPositions)
    }
}