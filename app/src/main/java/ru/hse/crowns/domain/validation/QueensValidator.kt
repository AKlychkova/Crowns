package ru.hse.crowns.domain.validation

import ru.hse.crowns.domain.boards.QueensBoard

interface QueensValidator {
    fun check(board: QueensBoard): QueensMistake?
}