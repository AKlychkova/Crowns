package ru.hse.crowns

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.generation.dancingLinks.NQueensDLUniqueChecker

class NQueensDLUniqueCheckerTest {
    private val checker = NQueensDLUniqueChecker()

    @Test
    fun nonUniqueTest() = runTest {
        // Arrange
        val board = NQueensBoard(4, emptyList())

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertFalse(result)
    }

    @Test
    fun uniqueTest() = runTest {
        // Arrange
        val board = NQueensBoard(4, listOf(Pair(0, 1)))

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertTrue(result)
    }
}