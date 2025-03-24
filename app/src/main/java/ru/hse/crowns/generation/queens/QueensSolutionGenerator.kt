package ru.hse.crowns.generation.queens

interface QueensSolutionGenerator {
    /**
     * Generate a random solution to the Queens puzzle for an [n][boardSize]-by-[n][boardSize] board
     * @param boardSize n
     * @return generated board
     */
    fun generateSolution(boardSize: Int) : QueensBoard
}