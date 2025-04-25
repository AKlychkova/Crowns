package ru.hse.crowns.domain.hints.queens

import ru.hse.crowns.domain.domainObjects.boards.QueensBoard

interface QueensHintsProvider {
    fun provideHint(board: QueensBoard) : QueensHint
}