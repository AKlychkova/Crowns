package ru.hse.crowns.domain.generation.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard

interface NQueensSolutionGenerator {
    /**
     * Generate a random solution to the N Queens puzzle for an [n][boardSize]-by-[n][boardSize] board
     * @param boardSize n
     * @return generated board
     */
    suspend fun generateSolution(boardSize: Int) : NQueensBoard
}