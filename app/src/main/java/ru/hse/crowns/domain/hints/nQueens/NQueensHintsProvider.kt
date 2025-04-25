package ru.hse.crowns.domain.hints.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard

interface NQueensHintsProvider {
    fun provideHint(board: NQueensBoard) : NQueensHint
}