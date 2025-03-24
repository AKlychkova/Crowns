package ru.hse.crowns.generation.nQueens

interface NQueensSolutionGenerator {
    /**
     * Generate a random solution to the N Queens puzzle for an [n][boardSize]-by-[n][boardSize] board
     * @param boardSize n
     * @return generated board
     */
    fun generateSolution(boardSize: Int) : NQueensBoard
}