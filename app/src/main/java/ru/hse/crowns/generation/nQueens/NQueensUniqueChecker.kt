package ru.hse.crowns.generation.nQueens

interface NQueensUniqueChecker {
    /**
     * @return true if [board] has no more than one solution, otherwise false
     */
    fun check(board: NQueensBoard) : Boolean
}