package ru.hse.crowns.domain.generation.queens

import ru.hse.crowns.domain.domainObjects.boards.QueensBoard

interface QueensSolutionGenerator {
    /**
     * Generate a random solution to the Queens puzzle for an [n][boardSize]-by-[n][boardSize] board
     * @param boardSize n
     * @return generated board
     */
    suspend fun generateSolution(boardSize: Int) : QueensBoard
}