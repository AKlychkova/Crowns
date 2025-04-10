package ru.hse.crowns.domain.boards

/**
 * @property size board dimension
 * @property queenPositions queens coordinates
 */
class NQueensBoard(val size: Int, queenPositions: Collection<Pair<Int, Int>>) : ObservableBoard {
    /**
     * Set of coordinates of all queens that are currently on the board
     */
    private var queenPositions: MutableSet<Pair<Int, Int>> = queenPositions.toHashSet()

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
     * The list of observers
     */
    private val observers = ArrayList<BoardObserver>()

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