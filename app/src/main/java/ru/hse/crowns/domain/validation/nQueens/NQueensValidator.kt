package ru.hse.crowns.domain.validation.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.NQueensMistake

interface NQueensValidator {
    /**
     * Check if there are mistake in the [board]
     * @param board board to check
     * @return mistake if it is found, otherwise null
     */
    fun check(board: NQueensBoard): NQueensMistake?
}