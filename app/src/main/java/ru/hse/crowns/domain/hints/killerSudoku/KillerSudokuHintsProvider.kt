package ru.hse.crowns.domain.hints.killerSudoku

import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard

interface KillerSudokuHintsProvider {
    fun provideHint(board: KillerSudokuBoard) : KillerSudokuHint
}