package ru.hse.crowns.generation.killerSudoku

/**
 * @property maxToClear maximum number of empty cells in generated Killer Sudoku level
 */
class KillerSudokuGenerator(private val maxToClear: Int,
                            private val uniqueChecker: KillerSudokuUniqueChecker,
                            private val solutionGenerator: KillerSudokuSolutionGenerator
) {

    /**
     * Randomly clear no more than [maxToClear] cells without violating the unique solution condition
     * @param board board where cells must be cleared
     */
    private fun clearCells(board: KillerSudokuBoard) {
        for (cell in (0 until board.size * board.size).shuffled()) {
            if (board.emptyCellsCount >= maxToClear) {
                break
            }
            val cellValue : Int = board.getValue(cell / board.size, cell % board.size)!!
            board.clearCell(cell / board.size, cell % board.size)
            if (!uniqueChecker.check(board)) {
                board.fillCell(
                    cell / board.size,
                    cell % board.size,
                    cellValue
                )
            }
        }
    }

    /**
     * Generate level of "Killer Sudoku" puzzle
     * @return generated level
     */
    fun generate() : KillerSudokuBoard {
        // Generate valid solution
        val board: KillerSudokuBoard = solutionGenerator.generateSolution()
        // Clear some cells
        clearCells(board)
        return board
    }
}