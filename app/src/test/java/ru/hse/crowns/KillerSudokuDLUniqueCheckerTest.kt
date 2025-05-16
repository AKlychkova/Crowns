package ru.hse.crowns

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.dancingLinks.KillerSudokuDLUniqueChecker

class KillerSudokuDLUniqueCheckerTest {
    private val checker = KillerSudokuDLUniqueChecker()

    @Test
    fun nonUnique4by4Test() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 2, 3, 3),
            intArrayOf(2, 2, 3, 3)
        )
        val sum = IntArray(4) { 10 }
        val grid: Array<IntArray> = Array(4) { IntArray(4) { 0 } }
        val board = KillerSudokuBoard(
            sudokuGrid = grid,
            polyominoDivision = division,
            polyominoSum = sum
        )

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertFalse(result)
    }

    @Test
    fun nonUnique9by9Test() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 0, 1, 1, 1, 2, 2, 2),
            intArrayOf(0, 0, 0, 1, 1, 1, 2, 2, 2),
            intArrayOf(0, 0, 0, 1, 1, 1, 2, 2, 2),
            intArrayOf(3, 3, 3, 4, 4, 4, 5, 5, 5),
            intArrayOf(3, 3, 3, 4, 4, 4, 5, 5, 5),
            intArrayOf(3, 3, 3, 4, 4, 4, 5, 5, 5),
            intArrayOf(6, 6, 6, 7, 7, 7, 8, 8, 8),
            intArrayOf(6, 6, 6, 7, 7, 7, 8, 8, 8),
            intArrayOf(6, 6, 6, 7, 7, 7, 8, 8, 8)
        )
        val sum = IntArray(9) { 45 }
        val grid: Array<IntArray> = Array(9) { IntArray(9) { 0 } }
        val board = KillerSudokuBoard(
            sudokuGrid = grid,
            polyominoDivision = division,
            polyominoSum = sum
        )

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertFalse(result)
    }

    @Test
    fun unique4by4Test() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 2, 3, 3),
            intArrayOf(2, 2, 3, 3)
        )
        val sum = IntArray(4) { 10 }
        val grid: Array<IntArray> = arrayOf(
            intArrayOf(1, 0, 0, 4),
            intArrayOf(0, 0, 2, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(3, 0, 0, 2)
        )
        val board = KillerSudokuBoard(
            sudokuGrid = grid,
            polyominoDivision = division,
            polyominoSum = sum
        )

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertTrue(result)
    }

    @Test
    fun unique4by4PolyominoTest() = runTest {
        // Arrange
        val division: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 3, 3, 4),
            intArrayOf(5, 5, 6, 6),
            intArrayOf(7, 7, 8, 8)
        )
        val sum = intArrayOf(5, 7, 3, 6, 4, 5, 5, 4, 4)
        val grid: Array<IntArray> = Array(4) { IntArray(4) { 0 } }
        val board = KillerSudokuBoard(
            sudokuGrid = grid,
            polyominoDivision = division,
            polyominoSum = sum
        )

        // Act
        val result: Boolean = checker.check(board)

        // Assert
        assertTrue(result)
    }
}