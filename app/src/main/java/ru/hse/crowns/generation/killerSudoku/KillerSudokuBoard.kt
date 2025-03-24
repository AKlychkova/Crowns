package ru.hse.crowns.generation.killerSudoku

import kotlin.math.roundToInt
import kotlin.math.sqrt

class KillerSudokuBoard(
    private val sudokuGrid: Array<IntArray>
) {
    var emptyCellsCount: Int = 0
        private set

    /**
     * Board dimension
     */
    val size: Int = sudokuGrid.size

    /**
     * Dimension of small squares (aka boxes)
     */
    val boxSize: Int = sqrt(size.toDouble()).roundToInt()

    /**
     * Possible cell values
     */
    val values = 1..size

    /**
     * Number of small squares (aka boxes) in one row
     */
    val boxesInRow = size / boxSize

    /**
     * Index corresponds to the polyomino id.
     * Value corresponds to the sum of the elements included in the corresponding polyomino.
     */
    private val polyominoSum = ArrayList<Int>()

    /**
     * Index corresponds to the polyomino id.
     * Each value is a list of coordinates of the elements included in the corresponding polyomino.
     */
    private val polyominoCoordinates = ArrayList<ArrayList<Pair<Int, Int>>>()

    /**
     * Stores the polyomino id for each cell
     */
    private val polyominoDivision: Array<IntArray>

    /**
     * @return number of polyomino into which the board is divided
     */
    fun getPolyominoCount(): Int {
        return polyominoCoordinates.size
    }

    init {
        // check if all values are valid
        for (i in sudokuGrid.indices) {
            for (j in sudokuGrid[i].indices) {
                if (sudokuGrid[i][j] !in values) {
                    throw IllegalArgumentException()
                }
            }
        }
        // randomly divide the board into polyominoes
        polyominoDivision = generatePolyominoDivision()
    }

    /**
     * Clear cell with the coordinates ([row], [col]).
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun clearCell(row: Int, col: Int) {
        if (row in sudokuGrid.indices &&
            col in sudokuGrid.indices
        ) {
            emptyCellsCount += 1
            sudokuGrid[row][col] = 0
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * Fill ([row], [col]) cell with the [value].
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     * @throws IllegalArgumentException if value is not valid
     * @throws IllegalArgumentException if cell is not empty
     *
     */
    fun fillCell(row: Int, col: Int, value: Int) {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        if (value !in values) {
            throw IllegalArgumentException("Value is not valid.")
        }
        if (!isEmpty(row, col)) {
            throw IllegalArgumentException("Cell is not empty.")
        }
        emptyCellsCount -= 1
        sudokuGrid[row][col] = value
    }

    /**
     * @return index of a box by cell coordinates ([row], [col])
     */
    fun getBoxId(row: Int, col: Int): Int {
        return if (row in sudokuGrid.indices &&
            col in sudokuGrid.indices
        ) {
            row / boxSize * boxesInRow + col / boxSize
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * @return a 2D array contained cells' values
     */
    fun getSudokuGrid(): Array<IntArray> {
        return Array(size) { i: Int -> sudokuGrid[i].clone() }
    }

    /**
     * @return value of cell with the coordinates ([row], [col]) or null if cell is empty
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun getValue(row: Int, col: Int): Int? {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        return if (!isEmpty(row, col)) sudokuGrid[row][col] else null
    }

    /**
     * Randomly divide the board into polyomino
     * @return a 2D array, each value in which corresponds to polyomino id for cell with the same coordinates
     */
    private fun generatePolyominoDivision(): Array<IntArray> {
        val division = Array(size) { IntArray(size) { -1 } }

        /**
         * Index corresponds to the polyomino id.
         * Value is a set of the elements included in the corresponding polyomino.
         */
        val polyominoValues = ArrayList<MutableSet<Int>>()

        // Go through all cells randomly
        for (cell: Int in (0 until size * size).shuffled()) {
            // Cell can be included to the same polyomino as its up, down, left or right neighbour or start new one
            val possiblePolyomino = ArrayList<Int>(5)

            // Add value corresponded to new polyomino (not equal to existing polyomino ids)
            possiblePolyomino.add(polyominoValues.size)

            val row: Int = cell / size
            val col: Int = cell % size

            // Go through cell's neighbours
            for (i in 1 until 9 step 2) {
                val neighbourRow = row + i / 3 - 1
                val neighbourCol = col + i % 3 - 1

                // Check if neighbour is already assigned with polyomino id and
                // if current cell can be added to this polyomino
                // (there are not two cells with the same values in one polyomino).
                if (neighbourRow in 0 until size &&
                    neighbourCol in 0 until size &&
                    division[neighbourRow][neighbourCol] != -1 &&
                    !polyominoValues[division[neighbourRow][neighbourCol]].contains(sudokuGrid[row][col])
                ) {
                    possiblePolyomino.add(division[neighbourRow][neighbourCol])
                }
            }

            // Randomly assign one of possible polyomino ids to the cell
            division[row][col] = possiblePolyomino.random()
            if (division[row][col] < polyominoValues.size) {
                polyominoValues[division[row][col]].add(sudokuGrid[row][col])
                polyominoSum[division[row][col]] += sudokuGrid[row][col]
                polyominoCoordinates[division[row][col]].add(Pair(row, col))
            } else {
                polyominoValues.add(mutableSetOf(sudokuGrid[row][col]))
                polyominoSum.add(sudokuGrid[row][col])
                polyominoCoordinates.add(arrayListOf(Pair(row, col)))
            }
        }
        return division
    }

    /**
     * @return polyomino id assigned to the cell with coordinates ([row], [col])
     * @throws IndexOutOfBoundsException if row or col are out of board bounds
     */
    fun getPolyomino(row: Int, col: Int): Int {
        return if (row in 0 until size &&
            col in 0 until size
        ) {
            polyominoDivision[row][col]
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * @return sum of elemnts included in polyomino with [polyominoId],
     * 0 if [polyominoId] does not correspond to any of existing polyominoes
     */
    fun getSum(polyominoId: Int): Int {
        return if (polyominoId in 0 until getPolyominoCount()) {
            polyominoSum[polyominoId]
        } else {
            0
        }
    }

    /**
     * @return coordinates of elemnts included in polyomino with [polyominoId],
     * empyt list if [polyominoId] does not correspond to any of existing polyominoes
     */
    fun getPolyominoCoordinates(polyominoId: Int): List<Pair<Int, Int>> {
        return if (polyominoId in 0 until getPolyominoCount()) {
            polyominoCoordinates[polyominoId].toList()
        } else {
            emptyList()
        }
    }

    /**
     * @return true if the cell in ([row], [col]) is empty (does not contain valid value), false otherwise
     */
    fun isEmpty(row: Int, col: Int): Boolean {
        return if (row in 0 until size &&
            col in 0 until size
        ) {
            sudokuGrid[row][col] !in values
        } else {
            throw IndexOutOfBoundsException()
        }
    }
}