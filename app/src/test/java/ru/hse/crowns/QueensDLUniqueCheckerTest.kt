package ru.hse.crowns

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.generation.dancingLinks.QueensDLUniqueChecker

class QueensDLUniqueCheckerTest {
    private val checker = QueensDLUniqueChecker()

    @Test
    fun nonUniqueTest() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 2, 3, 3),
            intArrayOf(2, 2, 3, 3),
        )
        val board = QueensBoard(4, emptyList(), division)

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertFalse(result)
    }

    @Test
    fun uniquePolyominoTest() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 2, 1, 1),
            intArrayOf(2, 2, 3, 3),
            intArrayOf(2, 2, 3, 3),
        )
        val board = QueensBoard(4, emptyList(), division)

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertTrue(result)
    }

    @Test
    fun uniqueQueensTest() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 2, 3, 3),
            intArrayOf(2, 2, 3, 3),
        )
        val board = QueensBoard(4, listOf(Pair(0, 1)), division)

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertTrue(result)
    }
}