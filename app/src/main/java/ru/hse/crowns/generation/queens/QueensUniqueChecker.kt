package ru.hse.crowns.generation.queens

interface QueensUniqueChecker {
    /**
     * @return true if [board] has no more than one solution, otherwise false
     */
    fun check(board: QueensBoard) : Boolean
}