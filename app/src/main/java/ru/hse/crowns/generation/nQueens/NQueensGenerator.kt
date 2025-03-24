package ru.hse.crowns.generation.nQueens

import ru.hse.crowns.boards.NQueensBoard

class NQueensGenerator(
    private val boardSize: Int,
    private val uniqueChecker: NQueensUniqueChecker,
    private val solutionGenerator: NQueensSolutionGenerator
) {

    /**
     * Removes random queens without violating the unique solution condition
     * @param board board from which queens must be removed
     */
    private fun removeQueens(board: NQueensBoard) {
        val solutionQueenPositions = board.getQueenPositions()
        // Go through the queens in random order
        for (queen in solutionQueenPositions.shuffled()) {
            // Remove the queen if it does not violate the uniqueness condition
            board.removeQueen(queen.first, queen.second)
            if (!uniqueChecker.check(board)) {
                board.addQueen(queen.first, queen.second)
            }
        }
    }

    /**
     * Generate level of "N Queens" puzzle
     * @return generated level
     */
    fun generate(): NQueensBoard {
        // Generate valid solution
        val board = solutionGenerator.generateSolution(boardSize)
        // Remove some queens
        removeQueens(board)
        return board
    }
}