package ru.hse.crowns.domain.generation.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.generation.Generator

class NQueensGenerator(
    private val uniqueChecker: NQueensUniqueChecker,
    private val solutionGenerator: NQueensSolutionGenerator
) : Generator<Int, NQueensBoard> {

    /**
     * Removes random queens without violating the unique solution condition
     * @param board board from which queens must be removed
     */
    private suspend fun removeQueens(board: NQueensBoard) {
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
     * Generate level of "N Queens" puzzle
     * @param input board size
     * @return generated level
     */
    override suspend fun generate(input: Int): NQueensBoard {
        // Generate valid solution
        val board = solutionGenerator.generateSolution(input)
        // Remove some queens
        removeQueens(board)
        return board
    }
}