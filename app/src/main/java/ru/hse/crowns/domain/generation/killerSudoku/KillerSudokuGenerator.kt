package ru.hse.crowns.domain.generation.killerSudoku

import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.Generator
import kotlin.math.roundToInt

class KillerSudokuGenerator(private val uniqueChecker: KillerSudokuUniqueChecker,
                            private val solutionGenerator: KillerSudokuSolutionGenerator
) : Generator<Int, KillerSudokuBoard> {

    /**
     * Randomly clear no more than [maxToClear] cells without violating the unique solution condition
     * @param board board where cells must be cleared
     * @param maxToClear maximum number of empty cells in generated Killer Sudoku level
     */
    private suspend fun clearCells(maxToClear: Int, board: KillerSudokuBoard) {
        val clearPerIter: Int = (maxToClear * 0.1).roundToInt()
        for (toClear in (0 until board.size * board.size).shuffled().chunked(clearPerIter)) {
            if (board.emptyCellsCount + toClear.size >= maxToClear) {
                break
            }
            val cellsValues: List<Int>  = toClear.map { cell ->
                board.getValue(cell / board.size, cell % board.size)!!
            }
            toClear.forEach { cell ->
                board.clearCell(cell / board.size, cell % board.size)
            }
            if (!uniqueChecker.check(board)) {
                toClear.forEachIndexed { index, cell ->
                    board.fillCell(
                        cell / board.size,
                        cell % board.size,
                        cellsValues[index]
                    )
                }
            }
        }
    }

    /**
     * Generate level of "Killer Sudoku" puzzle
     * @param input maximum number of empty cells in generated Killer Sudoku level
     * @return generated level
     */
    override suspend fun generate(input: Int): KillerSudokuBoard {
        // Generate valid solution
        val board: KillerSudokuBoard = solutionGenerator.generateSolution()
        // Clear some cells
        clearCells(input, board)
        return board
    }
}