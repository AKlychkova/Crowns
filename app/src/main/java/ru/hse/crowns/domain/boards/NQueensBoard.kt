package ru.hse.crowns.domain.boards

/**
 * @property size board dimension
 * @property queenPositions queens coordinates
 */
class NQueensBoard(val size: Int, queenPositions: Collection<Pair<Int, Int>>) {
    /**
     * Set of queens coordinates
     */
    private var queenPositions: MutableSet<Pair<Int, Int>> = queenPositions.toHashSet()

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
     * Removes queen from cell with coordinates ([row], [column]).
     * If cell is empty, nothing happens.
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun removeQueen(row: Int, column: Int) {
        if (row !in 0 until size ||
            column !in 0 until size
        ) {
            throw IndexOutOfBoundsException()
        }
        queenPositions.remove(Pair(row, column))
    }

    /**
     * Add queen to cell with coordinates ([row], [column]) **without** checking the game rules.
     * @throws IndexOutOfBoundsException if row or column are out of board bounds
     */
    fun addQueen(row: Int, column: Int) {
        if (row in 0 until size && column in 0 until size) {
            queenPositions.add(Pair(row, column))
        } else {
            throw IndexOutOfBoundsException()
        }
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
}