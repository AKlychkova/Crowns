package ru.hse.crowns.domain.generation.killerSudoku

import ru.hse.crowns.domain.boards.KillerSudokuBoard

interface KillerSudokuSolutionGenerator {
    /**
     * Generate a random solution to the Killer Sudoku puzzle
     * @return generated board
     */
    suspend fun generateSolution() : KillerSudokuBoard
}