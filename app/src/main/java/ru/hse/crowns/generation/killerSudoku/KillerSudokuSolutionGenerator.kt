package ru.hse.crowns.generation.killerSudoku

import ru.hse.crowns.boards.KillerSudokuBoard

interface KillerSudokuSolutionGenerator {
    /**
     * Generate a random solution to the Killer Sudoku puzzle
     * @return generated board
     */
    fun generateSolution() : KillerSudokuBoard
}