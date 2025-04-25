package ru.hse.crowns.domain.validation.queens

import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.QueensMistake

interface QueensValidator {
    fun check(board: QueensBoard): QueensMistake?
}