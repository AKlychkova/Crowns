package ru.hse.crowns.domain.validation.queens

import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.QueensMistake

interface QueensValidator {
    /**
     * Check if there are mistake in the [board]
     * @param board board to check
     * @return mistake if it is found, otherwise null
     */
    fun check(board: QueensBoard): QueensMistake?
}