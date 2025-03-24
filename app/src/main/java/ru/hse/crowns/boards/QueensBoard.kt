package ru.hse.crowns.boards

/**
 * @property size board dimension
 * @property queenPositions queens coordinates
 */
class QueensBoard(val size: Int, queenPositions: Collection<Pair<Int, Int>>) {
    /**
     * Set of queens coordinates
     */
    private val queenPositions: MutableSet<Pair<Int, Int>> = queenPositions.toHashSet()

    /**
     * Index corresponds to the polyomino id.
     * Each value is a list of coordinates of the elements included in the corresponding polyomino.
     */
    private val polyominoCoordinates = Array(queenPositions.size) { ArrayList<Pair<Int, Int>>() }

    /**
     * Stores the polyomino id for each cell
     */
    private val polyominoDivision: Array<IntArray>

    init {
        // randomly divide the board into polyominoes
        polyominoDivision = generatePolyominoDivision()
    }

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
            division[newCell.first][newCell.second] = division[currentCell.first][currentCell.second]
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
}