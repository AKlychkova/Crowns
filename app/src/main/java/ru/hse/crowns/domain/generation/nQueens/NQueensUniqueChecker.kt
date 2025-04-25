package ru.hse.crowns.domain.generation.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard

interface NQueensUniqueChecker {
    /**
     * @return true if [board] has no more than one solution, otherwise false
     */
    suspend fun check(board: NQueensBoard) : Boolean
}