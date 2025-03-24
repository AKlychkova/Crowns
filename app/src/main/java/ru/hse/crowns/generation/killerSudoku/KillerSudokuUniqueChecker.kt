package ru.hse.crowns.generation.killerSudoku

interface KillerSudokuUniqueChecker {
    /**
     * @return true if [board] has no more than one solution, otherwise false
     */
    fun check(board: KillerSudokuBoard) : Boolean
}