package ru.hse.crowns.domain.hints.queens

import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.domain.validation.gameStatuses.QueensMistake
import ru.hse.crowns.domain.validation.queens.QueensValidator
import ru.hse.crowns.utils.getPowerSet
import kotlin.math.max
import kotlin.math.min

class QueensHintsProviderImpl(private val validator: QueensValidator) : QueensHintsProvider {

    private fun findMissingCrosses(board: QueensBoard) = sequence<Pair<Int, Int>> {
        for (queenPosition in board.getQueenPositions()) {
            // Check row
            for (i in 0 until board.size) {
                if (board.getStatus(queenPosition.first, i) == QueenCellStatus.EMPTY) {
                    yield(Pair(queenPosition.first, i))
                }
            }

            // Check column
            for (i in 0 until board.size) {
                if (board.getStatus(i, queenPosition.second) == QueenCellStatus.EMPTY) {
                    yield(Pair(i, queenPosition.second))
                }
            }

            // Check polyominoes
            for (position in board.getPolyominoCoordinates(
                board.getPolyomino(queenPosition.first, queenPosition.second)
            )) {
                if (board.getStatus(position.first, position.second) == QueenCellStatus.EMPTY) {
                    yield(position)
                }
            }

            // Check diagonals
            for (i in listOf(-1, 1)) {
                for (j in listOf(-1, 1)) {
                    if (
                        queenPosition.first + i in 0 until board.size &&
                        queenPosition.second + j in 0 until board.size &&
                        board.getStatus(
                            queenPosition.first + i,
                            queenPosition.second + j
                        ) == QueenCellStatus.EMPTY
                    ) {
                        yield(Pair(queenPosition.first + i, queenPosition.second + j))
                    }
                }
            }
        }
    }

    /**
     * @return position of the cell if it is the only one empty cell in the row
     */
    private fun findOneEmptyInRow(board: QueensBoard): Pair<Int, Int>? {
        for (rowId in 0 until board.size) {
            if (board.getRow(rowId).count { status -> status == QueenCellStatus.EMPTY } == 1) {
                val colId = board.getRow(rowId)
                    .indexOfFirst { status -> status == QueenCellStatus.EMPTY }
                return Pair(rowId, colId)
            }
        }
        return null
    }

    /**
     * @return position of the cell if it is the only one empty cell in the column
     */
    private fun findOneEmptyInColumn(board: QueensBoard): Pair<Int, Int>? {
        for (colId in 0 until board.size) {
            if (board.getColumn(colId).count { status -> status == QueenCellStatus.EMPTY } == 1) {
                val rowId = board.getColumn(colId)
                    .indexOfFirst { status -> status == QueenCellStatus.EMPTY }
                return Pair(rowId, colId)
            }
        }
        return null
    }

    /**
     * @return position of the cell if it is the only one empty cell in the polyomino
     */
    private fun findOneEmptyInPolyomino(board: QueensBoard): Pair<Int, Int>? {
        for (polyominoId in 0 until board.getPolyominoCount()) {
            if (board.getPolyominoCoordinates(polyominoId).count { pos ->
                    board.getStatus(pos.first, pos.second) == QueenCellStatus.EMPTY
                } == 1) {
                val position = board.getPolyominoCoordinates(polyominoId)
                    .first { pos ->
                        board.getStatus(pos.first, pos.second) == QueenCellStatus.EMPTY
                    }
                return position
            }
        }
        return null
    }

    private fun findExclusionRowsZone(board: QueensBoard): QueensHint.ExclusionZone? {
        val emptyCounts = Array<MutableList<Int>>(board.size) { mutableListOf() }
        for (rowId in 0 until board.size) {
            emptyCounts[rowId].addAll(
                board.getRow(rowId)
                    .withIndex()
                    .filter { it.value == QueenCellStatus.EMPTY }
                    .map { it.index }
            )
        }
        val byValue = emptyCounts
            .withIndex()
            .groupBy { it.value }
            .filter { it.key.size == it.value.size }
        if (byValue.isNotEmpty()) {
            val key = byValue.keys.first()
            val zone = mutableListOf<Pair<Int, Int>>()
            byValue[key]?.forEach { row ->
                row.value.forEach { col ->
                    zone.add(Pair(row.index, col))
                }
            }
            val exclusion = mutableListOf<Pair<Int, Int>>()
            key.forEach { col ->
                exclusion.addAll(
                    (0 until board.size).map { Pair(it, col) }
                )
            }
            if (exclusion.any { pos ->
                    pos !in zone && board.getStatus(pos.first, pos.second) == QueenCellStatus.EMPTY
                }
            ) {
                return QueensHint.ExclusionZone(
                    zone,
                    exclusion,
                    key.size
                )
            }
        }
        return null
    }

    private fun findExclusionColumnsZone(board: QueensBoard): QueensHint.ExclusionZone? {
        val emptyCounts = Array<MutableList<Int>>(board.size) { mutableListOf() }
        for (colId in 0 until board.size) {
            emptyCounts[colId].addAll(
                board.getColumn(colId)
                    .withIndex()
                    .filter { it.value == QueenCellStatus.EMPTY }
                    .map { it.index }
            )
        }
        val byValue = emptyCounts
            .withIndex()
            .groupBy { it.value }
            .filter { it.key.size == it.value.size }
        if (byValue.isNotEmpty()) {
            val key = byValue.keys.first()
            val zone = mutableListOf<Pair<Int, Int>>()
            byValue[key]?.forEach { col ->
                col.value.forEach { row ->
                    zone.add(Pair(row, col.index))
                }
            }
            val exclusion = mutableListOf<Pair<Int, Int>>()
            key.forEach { row ->
                exclusion.addAll(
                    (0 until board.size).map { Pair(row, it) }
                )
            }
            if (exclusion.any { pos ->
                    pos !in zone && board.getStatus(pos.first, pos.second) == QueenCellStatus.EMPTY
                }
            ) {
                return QueensHint.ExclusionZone(
                    zone,
                    exclusion,
                    key.size
                )
            }
        }
        return null
    }

    private fun findExclusionPolyominoZone(board: QueensBoard): QueensHint.ExclusionZone? {
        val polyominoSubsets = (0 until board.getPolyominoCount()).toSet().getPowerSet().filter {
            it.isNotEmpty() && it.size < board.getPolyominoCount()
        }
        for (subset in polyominoSubsets) {
            val coordinates = subset.map { polyominoId ->
                board.getPolyominoCoordinates(polyominoId)
                    .filter { board.getStatus(it.first, it.second) == QueenCellStatus.EMPTY }
            }
            if (coordinates.any { it.isEmpty() }) {
                continue
            }
            var minRow: Int = board.size
            var maxRow: Int = -1
            var minCol: Int = board.size
            var maxCol: Int = -1

            for (polyomino in coordinates) {
                minRow = min(minRow, polyomino.minOf { it.first })
                maxRow = max(maxRow, polyomino.maxOf { it.first })
                minCol = min(minCol, polyomino.minOf { it.second })
                maxCol = max(maxCol, polyomino.maxOf { it.second })
            }

            if (subset.size == maxRow - minRow + 1) {
                val exclusion = (minRow..maxRow).map { rowId ->
                    (0 until board.size).map { colId ->
                        Pair(rowId, colId)
                    }
                }.flatten()
                val zone = coordinates.flatten()
                if (exclusion.any {
                        it !in zone && board.getStatus(
                            it.first,
                            it.second
                        ) == QueenCellStatus.EMPTY
                    }) {
                    return QueensHint.ExclusionZone(
                        zone = zone,
                        exclusion = exclusion,
                        queensAmount = subset.size
                    )
                }
            }

            if (subset.size == maxCol - minCol + 1) {
                val exclusion = (minCol..maxCol).map { colId ->
                    (0 until board.size).map { rowId ->
                        Pair(rowId, colId)
                    }
                }.flatten()
                val zone = coordinates.flatten()
                if (exclusion.any {
                        it !in zone && board.getStatus(
                            it.first,
                            it.second
                        ) == QueenCellStatus.EMPTY
                    }) {
                    return QueensHint.ExclusionZone(
                        zone = zone,
                        exclusion = exclusion,
                        queensAmount = subset.size
                    )
                }
            }
        }
        return null
    }

    private fun findBreakingRulesPlacement(board: QueensBoard): QueensHint.RuleBreakingPlacement? {
        val copy = board.clone()
        for (row in 0 until copy.size) {
            for (col in 0 until copy.size) {
                if (board.getStatus(row, col) == QueenCellStatus.EMPTY) {
                    val crosses = mutableListOf<Pair<Int, Int>>()
                    val queens = mutableListOf<Pair<Int, Int>>()
                    fun addCrosses() {
                        findMissingCrosses(copy).forEach { pos ->
                            copy.setCross(pos.first, pos.second)
                            crosses.add(pos)
                        }
                    }

                    fun addQueen(pos: Pair<Int, Int>) {
                        copy.addQueen(pos.first, pos.second)
                        queens.add(pos)
                    }

                    fun checkStatus(): QueensHint.RuleBreakingPlacement? {
                        val status = validator.check(copy)
                        if (status is QueensMistake) {
                            return QueensHint.RuleBreakingPlacement(
                                Pair(row, col),
                                status.positions
                            )
                        }
                        return null
                    }

                    addQueen(Pair(row, col))
                    addCrosses()
                    checkStatus()?.let { return it }
                    var position = findOneEmptyInRow(copy)
                        ?: findOneEmptyInColumn(copy)
                        ?: findOneEmptyInPolyomino(copy)
                    while (position != null) {
                        addQueen(position)
                        addCrosses()
                        checkStatus()?.let { return it }
                        position = findOneEmptyInRow(copy) ?: findOneEmptyInColumn(copy)
                    }
                    queens.forEach { pos -> copy.clearCell(pos.first, pos.second) }
                    crosses.forEach { pos -> copy.clearCell(pos.first, pos.second) }
                }
            }
        }
        return null
    }

    override fun provideHint(board: QueensBoard): QueensHint {
        val positions = findMissingCrosses(board).toList()
        if (positions.isNotEmpty()) {
            return QueensHint.MissingCrosses(positions)
        }
        findOneEmptyInRow(board)?.let { empty ->
            val row = (0 until board.size)
                .filterNot { it == empty.second }
                .map { Pair(empty.first, it) }
            return QueensHint.OneEmptyInRow(empty, row)
        }
        findOneEmptyInColumn(board)?.let { empty ->
            val col = (0 until board.size)
                .filterNot { it == empty.first }
                .map { Pair(it, empty.second) }
            return QueensHint.OneEmptyInColumn(empty, col)
        }
        findOneEmptyInPolyomino(board)?.let { empty ->
            val polyomino =
                board.getPolyominoCoordinates(board.getPolyomino(empty.first, empty.second))
            return QueensHint.OneEmptyInPolyomino(empty, polyomino)
        }

        findExclusionPolyominoZone(board)?.let { return it }
        findExclusionRowsZone(board)?.let { return it }
        findExclusionColumnsZone(board)?.let { return it }
        findBreakingRulesPlacement(board)?.let { return it }

        return QueensHint.Undefined
    }


}