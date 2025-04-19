package ru.hse.crowns.domain.boards

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * @property sudokuGrid a 2D array contains values of the board cells.
 * Values are positive if they are 'original', negative if they are 'user', 0 for empty cells.
 */
class KillerSudokuBoard internal constructor(
    private val sudokuGrid: Array<IntArray>,
    polyominoDivision: Array<IntArray>?,
    polyominoSum: IntArray?
) : ObservableBoard {
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
    private var polyominoSum: MutableList<Int> = ArrayList<Int>()

    /**
     * Index corresponds to the polyomino id.
     * Each value is a list of coordinates of the elements included in the corresponding polyomino.
     */
    private var polyominoCoordinates: MutableList<MutableList<Pair<Int, Int>>> = ArrayList()

    /**
     * Stores the polyomino id for each cell
     */
    private val polyominoDivision: Array<IntArray>

    /**
     * A 2D array contains lists of notes values for each cell.
     */
    private val notes = Array<Array<MutableSet<Int>>>(size) { Array(size) { HashSet() } }

    /**
     * The list of observers
     */
    private val observers = ArrayList<BoardObserver>()

    /**
     * @return number of polyomino into which the board is divided
     */
    fun getPolyominoCount(): Int {
        return polyominoCoordinates.size
    }

    init {
        // check if all values are valid
        for (i in sudokuGrid.indices) {
            for ( j in sudokuGrid[i].indices) {
                if (abs(sudokuGrid[i][j]) !in values && sudokuGrid[i][j] != 0) {
                    throw IllegalArgumentException()
                } else if (sudokuGrid[i][j] == 0) {
                    emptyCellsCount += 1
                }
            }
        }
        // check division
        if (polyominoDivision != null && polyominoSum != null) {
            this.polyominoDivision = polyominoDivision
            this.polyominoSum = polyominoSum.toMutableList()
            val maxId = polyominoDivision.maxOf { row -> row.max() }
            polyominoCoordinates = MutableList(maxId + 1) { ArrayList() }
            for ((rowIndex, row) in polyominoDivision.withIndex()) {
                for ((columnIndex, id) in row.withIndex()) {
                    polyominoCoordinates[id].add(Pair(rowIndex, columnIndex))
                }
            }
        } else {
            if(emptyCellsCount != 0) {
                throw IllegalArgumentException()
            }
            // randomly divide the board into polyominoes
            this.polyominoDivision = generatePolyominoDivision()
        }
    }

    constructor(sudokuGrid: Array<IntArray>) : this(sudokuGrid, null, null)

    /**
     * Clear cell with the coordinates ([row], [col]).
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun clearCell(row: Int, col: Int) {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        if (!isEmpty(row, col)) {
            emptyCellsCount += 1
            sudokuGrid[row][col] = 0
        }
        clearNotes(row, col)
        // claerNotes has already called notifyObservers()
    }

    /**
     * Fill ([row], [col]) cell with the [value].
     * The value will be considered 'original', to add 'user' value use [fillCellUser] function.
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     * @throws IllegalArgumentException if value is not valid
     * @throws IllegalArgumentException if cell is not empty
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
        notifyObservers(row, col)
    }

    /**
     * Fill ([row], [col]) cell with the [value], which will be considered 'user'.
     * Value can replace another 'user' value, but not the original one.
     * All notes will be removed.
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     * @throws IllegalArgumentException if value is not valid
     * @throws IllegalArgumentException if there is an 'original' value in the cell cell
     */
    fun fillCellUser(row: Int, col: Int, value: Int) {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        if (value !in values) {
            throw IllegalArgumentException("Value is not valid.")
        }
        if (isOriginal(row, col)) {
            throw IllegalArgumentException("Cell contains 'original' value.")
        }
        clearNotes(row, col)
        emptyCellsCount -= 1
        sudokuGrid[row][col] = -value
        notifyObservers(row, col)
    }

    /**
     * @return true if the cell in ([row], [col]) contains 'original' value, false otherwise
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun isOriginal(row: Int, col: Int): Boolean {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        return !isEmpty(row, col) && sudokuGrid[row][col] > 0
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
     * @return Sequence of values in the box
     * @param boxId id of the box
     * @throws IndexOutOfBoundsException if boxId is out of the bounds
     */
    fun getBox(boxId: Int) = sequence<Int> {
        if (boxId !in 0 until boxesInRow * boxesInRow) {
            throw IndexOutOfBoundsException()
        }
        val row: Int = boxId / boxesInRow * boxSize
        val column: Int = boxId % boxesInRow * boxSize
        for (i in 0 until boxSize) {
            for (j in 0 until boxSize) {
                yield(abs(sudokuGrid[row + i][column + j]))
            }
        }
    }

    /**
     * @return a 2D array contained cells' values.
     * There is not difference between 'original' and 'user' values.
     */
    fun getSudokuGrid(): Array<IntArray> {
        return Array(size) { i: Int -> sudokuGrid[i].map { value: Int -> abs(value) }.toIntArray() }
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
        return if (!isEmpty(row, col))
            abs(sudokuGrid[row][col])
        else
            null
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
                    !polyominoValues[division[neighbourRow][neighbourCol]].contains(abs(sudokuGrid[row][col]))
                ) {
                    possiblePolyomino.add(division[neighbourRow][neighbourCol])
                }
            }

            // Randomly assign one of possible polyomino ids to the cell
            division[row][col] = possiblePolyomino.random()
            if (division[row][col] < polyominoValues.size) {
                polyominoValues[division[row][col]].add(abs(sudokuGrid[row][col]))
                polyominoSum[division[row][col]] += abs(sudokuGrid[row][col])
                polyominoCoordinates[division[row][col]].add(Pair(row, col))
            } else {
                polyominoValues.add(mutableSetOf(abs(sudokuGrid[row][col])))
                polyominoSum.add(abs(sudokuGrid[row][col]))
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
     * empty list if [polyominoId] does not correspond to any of existing polyominoes
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
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun isEmpty(row: Int, col: Int): Boolean {
        return if (row in 0 until size &&
            col in 0 until size
        ) {
            abs(sudokuGrid[row][col]) !in values
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * Add note to the ([row], [col]) cell with [value]
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     * @throws IllegalArgumentException if value is not valid
     * @throws IllegalArgumentException if cell is not empty
     */
    fun addNote(row: Int, col: Int, value: Int) {
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
        notes[row][col].add(value)
        notifyObservers(row, col)
    }

    fun addAllNotes(row: Int, col: Int, values: Iterable<Int>) {
        for(value in values) {
            addNote(row, col, value)
        }
    }

    /**
     * Remove note with [value] from the ([row], [col]) cell if it is there,
     * otherwise nothing happens
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     * @throws IllegalArgumentException if value is not valid
     */
    fun removeNote(row: Int, col: Int, value: Int) {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        if (value !in values) {
            throw IllegalArgumentException("Value is not valid.")
        }
        if (notes[row][col].remove(value)) {
            notifyObservers(row, col)
        }
    }

    /**
     * Remove all notes from the ([row], [col]) cell and notify observers.
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun clearNotes(row: Int, col: Int) {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        notes[row][col].clear()
        notifyObservers(row, col)
    }

    /**
     * @return set of all notes in the ([row], [col]) cell
     * @throws IndexOutOfBoundsException if row or col are out of the board bounds
     */
    fun getNotes(row: Int, col: Int): Set<Int> {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        return notes[row][col].toSet()
    }

    /**
     * @return true if there is note with [value] in the ([row], [col]) cell, else false
     * @throws IndexOutOfBoundsException if [row] or [col] are out of the board bounds
     * @throws IllegalArgumentException if value is not valid
     */
    fun containNote(row: Int, col: Int, value: Int): Boolean {
        if (row !in sudokuGrid.indices ||
            col !in sudokuGrid.indices
        ) {
            throw IndexOutOfBoundsException()
        }
        if (value !in values) {
            throw IllegalArgumentException("Value is not valid.")
        }
        return notes[row][col].contains(value)
    }

    /**
     * @return [List] with row values
     * @param rowIndex index of the row
     * @throws IndexOutOfBoundsException if [rowIndex] is out of the board bounds
     */
    fun getRow(rowIndex: Int): List<Int> {
        if (rowIndex !in sudokuGrid.indices) {
            throw IndexOutOfBoundsException()
        }
        return sudokuGrid[rowIndex].map { value: Int -> abs(value) }
    }

    /**
     * @return [List] with column values
     * @param columnIndex index of the column
     * @throws IndexOutOfBoundsException if [columnIndex] is out of the board bounds
     */
    fun getColumn(columnIndex: Int): List<Int> {
        if (columnIndex !in sudokuGrid.indices) {
            throw IndexOutOfBoundsException()
        }
        val column = ArrayList<Int>(size)
        for (i in 0 until size) {
            column.add(abs(sudokuGrid[i][columnIndex]))
        }
        return column
    }

    override fun addObserver(observer: BoardObserver) {
        observers.add(observer)
    }

    override fun removeObserver(observer: BoardObserver) {
        observers.remove(observer)
    }

    override fun notifyObservers(row: Int, column: Int) {
        for (observer: BoardObserver in observers) {
            observer.onChanged(row, column)
        }
    }

    override fun clearObservers() {
        observers.clear()
    }
}