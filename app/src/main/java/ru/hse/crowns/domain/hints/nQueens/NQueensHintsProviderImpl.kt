package ru.hse.crowns.domain.hints.nQueens

import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus
import ru.hse.crowns.domain.validation.gameStatuses.NQueensMistake
import ru.hse.crowns.domain.validation.nQueens.NQueensValidator
import ru.hse.crowns.utils.max
import ru.hse.crowns.utils.min
import ru.hse.crowns.utils.plus
import kotlin.math.min

class NQueensHintsProviderImpl(private val validator: NQueensValidator) : NQueensHintsProvider {
    private fun findMissingCrosses(board: NQueensBoard) = sequence<Pair<Int, Int>> {
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

            // Check up diagonal
            for (i in -min(queenPosition.second, board.size - queenPosition.first - 1)..
                    min(queenPosition.first, board.size - queenPosition.second - 1)) {
                if (board.getStatus(queenPosition.first - i, queenPosition.second + i)
                    == QueenCellStatus.EMPTY
                ) {
                    yield(Pair(queenPosition.first - i, queenPosition.second + i))
                }
            }

            // Check down diagonal
            for (i in -queenPosition.min() until board.size - queenPosition.max()) {
                if (board.getStatus(queenPosition.first + i, queenPosition.second + i)
                    == QueenCellStatus.EMPTY
                ) {
                    yield(queenPosition + i)
                }
            }
        }
    }

    /**
     * @return position of the cell if it is the only one empty cell in the row
     */
    private fun findOneEmptyInRow(board: NQueensBoard): Pair<Int, Int>? {
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
    private fun findOneEmptyInColumn(board: NQueensBoard): Pair<Int, Int>? {
        for (colId in 0 until board.size) {
            if (board.getColumn(colId).count { status -> status == QueenCellStatus.EMPTY } == 1) {
                val rowId = board.getColumn(colId)
                    .indexOfFirst { status -> status == QueenCellStatus.EMPTY }
                return Pair(rowId, colId)
            }
        }
        return null
    }

    private fun findExclusionRowsZone(board: NQueensBoard): NQueensHint.RowExclusionZone? {
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
                return NQueensHint.RowExclusionZone(
                    zone,
                    exclusion,
                    key.size
                )
            }
        }
        return null
    }

    private fun findExclusionColumnsZone(board: NQueensBoard): NQueensHint.ColumnExclusionZone? {
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
                return NQueensHint.ColumnExclusionZone(
                    zone,
                    exclusion,
                    key.size
                )
            }
        }
        return null
    }

    private fun findBreakingRulesPlacement(board: NQueensBoard): NQueensHint.RuleBreakingPlacement? {
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

                    fun checkStatus(): NQueensHint.RuleBreakingPlacement? {
                        val status = validator.check(copy)
                        if (status is NQueensMistake) {
                            return NQueensHint.RuleBreakingPlacement(
                                Pair(row, col),
                                status.positions
                            )
                        }
                        return null
                    }

                    addQueen(Pair(row, col))
                    addCrosses()
                    checkStatus()?.let { return it }
                    var position = findOneEmptyInRow(copy) ?: findOneEmptyInColumn(copy)
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

    override fun provideHint(board: NQueensBoard): NQueensHint {
        val positions = findMissingCrosses(board).toList()
        if (positions.isNotEmpty()) {
            return NQueensHint.MissingCrosses(positions)
        }
        findOneEmptyInRow(board)?.let { empty ->
            val row = (0 until board.size)
                .filterNot { it == empty.second }
                .map { Pair(empty.first, it) }
            return NQueensHint.OneEmptyInRow(empty, row)
        }
        findOneEmptyInColumn(board)?.let { empty ->
            val col = (0 until board.size)
                .filterNot { it == empty.first }
                .map { Pair(it, empty.second) }
            return NQueensHint.OneEmptyInColumn(empty, col)
        }

        findExclusionRowsZone(board)?.let { return it }
        findExclusionColumnsZone(board)?.let { return it }
        findBreakingRulesPlacement(board)?.let { return it }

        return NQueensHint.Undefined()
    }


}