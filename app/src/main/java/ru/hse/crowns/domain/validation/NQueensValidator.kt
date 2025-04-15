package ru.hse.crowns.domain.validation

import ru.hse.crowns.domain.boards.NQueensBoard

interface NQueensValidator {
    fun check(board: NQueensBoard): NQueensMistake?
}