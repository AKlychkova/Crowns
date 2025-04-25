package ru.hse.crowns.domain.validation.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.NQueensMistake

interface NQueensValidator {
    fun check(board: NQueensBoard): NQueensMistake?
}