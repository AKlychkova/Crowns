package ru.hse.crowns.domain.validation

import ru.hse.crowns.domain.boards.KillerSudokuBoard

interface KillerSudokuValidator {
    fun check(board: KillerSudokuBoard): KillerSudokuMistake?
}