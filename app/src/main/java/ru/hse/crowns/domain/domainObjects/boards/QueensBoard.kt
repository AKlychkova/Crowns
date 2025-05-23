package ru.hse.crowns.domain.domainObjects.boards

/**
 * @property size board dimension
 * @property queenPositions queens coordinates
 * @property polyominoDivision a 2D array stores the polyomino id for each cell
 */
class QueensBoard(
    val size: Int,
    queenPositions: Collection<Pair<Int, Int>>,
    polyominoDivision: Array<IntArray>?
) : ObservableBoard {

    /**
     * Set of queens coordinates
     */
    private val queenPositions: MutableSet<Pair<Int, Int>> = queenPositions.filter { position ->
        position.first in 0 until size && position.second in 0 until size
    }.toHashSet()

    /**
     * A 2D array which hold cells' statuses
     */
    private val cellStatuses = Array(size) { row: Int ->
        Array(size) { column: Int ->
            if (Pair(row, column) in queenPositions)
                QueenCellStatus.ORIGINAL_QUEEN
            else
                QueenCellStatus.EMPTY
        }
    }

    /**
     * Index corresponds to the polyomino id.
     * Each value is a list of coordinates of the elements included in the corresponding polyomino.
     */
    private val polyominoCoordinates = Array(size) { ArrayList<Pair<Int, Int>>() }

    /**
     * Stores the polyomino id for each cell
     */
    private val polyominoDivision: Array<IntArray>

    /**
     * The list of observers
     */
    private val observers = ArrayList<BoardObserver>()

    init {
        if (polyominoDivision != null &&
            polyominoDivision.size == size &&
            polyominoDivision.all { row -> row.size == size && row.all { id -> id in 0 until size } }
        ) {
            this.polyominoDivision = polyominoDivision
            for ((rowIndex, row) in polyominoDivision.withIndex()) {
                for ((columnIndex, id) in row.withIndex()) {
                    polyominoCoordinates[id].add(Pair(rowIndex, columnIndex))
                }
            }
        } else {
            this.polyominoDivision = generatePolyominoDivision()
        }
    }

    constructor(size: Int, queenPositions: Collection<Pair<Int, Int>>) : this(
        size = size,
        queenPositions = queenPositions,
        polyominoDivision = null
    )

    /**
     * @param queenPositions an array in which each pair (index, value) corresponds to the coordinates of the queen (row, column)
     */
    constructor(queenPositions: IntArray) : this(
        queenPositions.size,
        queenPositions
            .mapIndexed { row: Int, col: Int -> Pair(row, col) }
            .filter { (row: Int, col: Int) -> col in queenPositions.indices }
    )

    /**
     * Clear the cell with coordinates ([row], [column]).
     * If cell is empty, nothing happens.
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun clearCell(row: Int, column: Int) {
        if (row !in 0 until size ||
            column !in 0 until size
        ) {
            throw IndexOutOfBoundsException()
        }
        queenPositions.remove(Pair(row, column))
        cellStatuses[row][column] = QueenCellStatus.EMPTY
        notifyObservers(row, column)
    }

    /**
     * Add queen to cell with coordinates ([row], [column]) **without** checking the game rules.
     * The queen will be considered 'original', to add 'user' queen use [addUserQueen] function.
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun addQueen(row: Int, column: Int) {
        if (row in 0 until size && column in 0 until size) {
            queenPositions.add(Pair(row, column))
            cellStatuses[row][column] = QueenCellStatus.ORIGINAL_QUEEN
            notifyObservers(row, column)
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * Add user queen to cell with coordinates ([row], [column]) **without** checking the game rules.
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun addUserQueen(row: Int, column: Int) {
        if (row !in 0 until size ||
            column !in 0 until size
        ) {
            throw IndexOutOfBoundsException()
        }
        queenPositions.add(Pair(row, column))
        cellStatuses[row][column] = QueenCellStatus.USER_QUEEN
        notifyObservers(row, column)
    }

    /**
     * Mark the cell with cross.
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun setCross(row: Int, column: Int) {
        if (row !in 0 until size ||
            column !in 0 until size
        ) {
            throw IndexOutOfBoundsException()
        }
        if (hasQueen(row, column)) {
            queenPositions.remove(Pair(row, column))
        }
        cellStatuses[row][column] = QueenCellStatus.CROSS
        notifyObservers(row, column)
    }

    /**
     * @return the number of queens in the board
     */
    fun getQueensCount(): Int = queenPositions.size

    /**
     * @return a list of queens coordinates
     */
    fun getQueenPositions(): List<Pair<Int, Int>> = queenPositions.toList()

    /**
     * Randomly divide the board into polyomino
     * @return a 2D array, each value in which corresponds to polyomino id for cell with the same coordinates
     */
    private fun generatePolyominoDivision(): Array<IntArray> {
        /**
         * Set of cells included in the polyominoes, presumably having a not included neighbor cell
         */
        val boundaryCells = HashSet<Pair<Int, Int>>()

        val division = Array(size) { IntArray(size) { -1 } }

        // As there is one and only one queen in each polyomino, start the distribution with them
        for ((index, position) in queenPositions.withIndex()) {
            division[position.first][position.second] = index
            polyominoCoordinates[index].add(position)
            boundaryCells.add(position)
        }

        /**
         * @return list of neighbours of the [cell] which are not in any polyomino yet
         */
        fun getFreeNeighbourCells(cell: Pair<Int, Int>): List<Pair<Int, Int>> {
            val cells = ArrayList<Pair<Int, Int>>()
            for (i in (1 until 9 step 2)) {
                val neighbourRow = cell.first + i / 3 - 1
                val neighbourCol = cell.second + i % 3 - 1
                if (neighbourRow in 0 until size &&
                    neighbourCol in 0 until size &&
                    division[neighbourRow][neighbourCol] == -1
                ) {
                    cells.add(Pair(neighbourRow, neighbourCol))
                }
            }
            return cells
        }

        while (boundaryCells.isNotEmpty()) {
            // Randomly choose boundary cell
            val currentCell = boundaryCells.random()
            // Get neighbour free cells
            val neighbourCells: List<Pair<Int, Int>> = getFreeNeighbourCells(currentCell)
            // Check if there is at least one free neighbour cell
            if (neighbourCells.isEmpty()) {
                boundaryCells.remove(currentCell)
                continue
            }
            // Put random free neighbour to polyomino of the current cell
            val newCell = neighbourCells.random()
            division[newCell.first][newCell.second] =
                division[currentCell.first][currentCell.second]
            polyominoCoordinates[division[newCell.first][newCell.second]].add(newCell)
            // If current cell is not boundary now, remove it from the set
            if (neighbourCells.size == 1) {
                boundaryCells.remove(currentCell)
            }
            // mark the included cell as boundary
            boundaryCells.add(newCell)
        }
        return division
    }

    /**
     * @return polyomino id assigned to the cell with coordinates ([row], [col])
     * @throws IndexOutOfBoundsException if row or col are out of board bounds
     */
    fun getPolyomino(row: Int, col: Int): Int {
        return if (row in 0 until size && col in 0 until size) {
            polyominoDivision[row][col]
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * @return coordinates of elemnts included in polyomino with [polyominoId],
     * empyt list if [polyominoId] does not correspond to any of existing polyominoes
     */
    fun getPolyominoCoordinates(polyominoId: Int): List<Pair<Int, Int>> {
        return if (polyominoId in 0 until size) {
            polyominoCoordinates[polyominoId].toList()
        } else {
            emptyList()
        }
    }

    /**
     * @return number of polyomino into which the board is divided
     */
    fun getPolyominoCount(): Int {
        return polyominoCoordinates.size
    }

    /**
     * @return true if there is a queen in the cell else false
     * @param row row of the cell
     * @param column column of the cell
     */
    fun hasQueen(row: Int, column: Int): Boolean = Pair(row, column) in queenPositions

    /**
     * @return status of the cell
     * @param row row of the cell
     * @param column column of the cell
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun getStatus(row: Int, column: Int): QueenCellStatus {
        if (row !in 0 until size ||
            column !in 0 until size
        ) {
            throw IndexOutOfBoundsException()
        }
        return cellStatuses[row][column]
    }

    /**
     * @return [List] with row statuses
     * @param rowIndex index of the row
     * @throws IndexOutOfBoundsException if [rowIndex] is out of the board bounds
     */
    fun getRow(rowIndex: Int): List<QueenCellStatus> {
        if (rowIndex !in cellStatuses.indices) {
            throw IndexOutOfBoundsException()
        }
        return cellStatuses[rowIndex].toList()
    }

    /**
     * @return [List] with column statuses
     * @param columnIndex index of the column
     * @throws IndexOutOfBoundsException if [columnIndex] is out of the board bounds
     */
    fun getColumn(columnIndex: Int): List<QueenCellStatus> {
        if (columnIndex !in cellStatuses.indices) {
            throw IndexOutOfBoundsException()
        }
        val column = ArrayList<QueenCellStatus>(size)
        for (i in 0 until size) {
            column.add(cellStatuses[i][columnIndex])
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

    fun clone(): QueensBoard {
        val newBoard = QueensBoard(
            size,
            emptyList(),
            polyominoDivision
        )
        for(i in 0 until size) {
            for(j in 0 until size) {
                when(cellStatuses[i][j]) {
                    QueenCellStatus.EMPTY -> {}
                    QueenCellStatus.ORIGINAL_QUEEN -> newBoard.addQueen(i, j)
                    QueenCellStatus.USER_QUEEN -> newBoard.addUserQueen(i, j)
                    QueenCellStatus.CROSS -> newBoard.setCross(i, j)
                }
            }
        }
        return newBoard
    }

    /**
     * Clear all not original cells
     */
    fun backToOriginal() {
        for(i in 0 until size) {
            for(j in 0 until size) {
                if(cellStatuses[i][j] != QueenCellStatus.ORIGINAL_QUEEN) {
                    clearCell(i, j)
                }
            }
        }
    }
}