package ru.hse.crowns.domain.generation.killerSudoku

import ru.hse.crowns.domain.boards.KillerSudokuBoard

interface KillerSudokuUniqueChecker {
    /**
     * @return true if [board] has no more than one solution, otherwise false
     */
    fun check(board: KillerSudokuBoard) : Boolean
}