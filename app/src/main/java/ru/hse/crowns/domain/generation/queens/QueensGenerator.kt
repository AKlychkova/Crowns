package ru.hse.crowns.domain.generation.queens

import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.generation.Generator

class QueensGenerator(private val uniqueChecker: QueensUniqueChecker,
                      private val solutionGenerator: QueensSolutionGenerator
) : Generator<Int, QueensBoard> {

    /**
     * Removes random queens without violating the unique solution condition
     * @param board board from which queens must be removed
     */
    private suspend fun removeQueens(board: QueensBoard) {
        val solutionQueenPositions = board.getQueenPositions()
        // Go through the queens in random order
        for (queen in solutionQueenPositions.shuffled()) {
            // Remove the queen if it does not violate the uniqueness condition
            board.clearCell(queen.first, queen.second)
            if (!uniqueChecker.check(board)) {
                board.addQueen(queen.first, queen.second)
            }
        }
    }

    /**
     * Generate level of "Queens" puzzle
     * @param input board size
     * @return generated level
     */
    override suspend fun generate(input: Int): QueensBoard {
        // Generate valid solution
        val board = solutionGenerator.generateSolution(input)
        // Remove some queens
        removeQueens(board)
        return board
    }
}