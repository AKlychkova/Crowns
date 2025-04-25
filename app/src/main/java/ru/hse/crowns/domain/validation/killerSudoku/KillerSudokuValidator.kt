package ru.hse.crowns.domain.validation.killerSudoku

import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.validation.gameStatuses.KillerSudokuMistake

interface KillerSudokuValidator {
    fun check(board: KillerSudokuBoard): KillerSudokuMistake?
}