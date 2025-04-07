package ru.hse.crowns.domain.generation.queens

import ru.hse.crowns.domain.boards.QueensBoard

interface QueensUniqueChecker {
    /**
     * @return true if [board] has no more than one solution, otherwise false
     */
    suspend fun check(board: QueensBoard) : Boolean
}