package ru.hse.crowns.generation.killerSudoku

interface KillerSudokuSolutionGenerator {
    /**
     * Generate a random solution to the Killer Sudoku puzzle
     * @return generated board
     */
    fun generateSolution() : KillerSudokuBoard
}