package ru.hse.crowns.domain.validation.killerSudoku

import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.validation.gameStatuses.KillerSudokuMistake

interface KillerSudokuValidator {
    /**
     * Check if there are mistake in the [board]
     * @param board board to check
     * @return mistake if it is found, otherwise null
     */
    fun check(board: KillerSudokuBoard): KillerSudokuMistake?
}