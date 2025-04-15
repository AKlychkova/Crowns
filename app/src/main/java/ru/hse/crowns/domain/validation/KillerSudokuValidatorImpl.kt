package ru.hse.crowns.domain.validation

import ru.hse.crowns.domain.boards.KillerSudokuBoard

class KillerSudokuValidatorImpl: KillerSudokuValidator {
    override fun check(board: KillerSudokuBoard): KillerSudokuMistake? {
        for (index in 0 until board.size) {
            val row = board.getRow(index)
            for (value in board.values) {
                val indices = row.indices.filter { row[it] == value }
                if (indices.size > 1) {
                    return KillerSudokuMistake.OneRow(
                        *indices
                            .map { col: Int -> Pair(index, col) }
                            .toTypedArray()
                    )
                }
            }
        }

        for (index in 0 until board.size) {
            val column = board.getColumn(index)
            for (value in board.values) {
                val indices = column.indices.filter { column[it] == value }
                if (indices.size > 1) {
                    return KillerSudokuMistake.OneColumn(
                        *indices
                            .map { row: Int -> Pair(row, index) }
                            .toTypedArray()
                    )
                }
            }
        }

        for (boxId in 0 until board.boxesInRow * board.boxesInRow) {
            val boxValues = board.getBox(boxId).toList()
            for (value in board.values) {
                val indices = boxValues.indices.filter { boxValues[it] == value }
                if (indices.size > 1) {
                    return KillerSudokuMistake.OneBox(
                        *indices.map { position: Int ->
                            Pair(
                                boxId / board.boxesInRow * board.boxSize + position / board.boxSize,
                                boxId % board.boxesInRow * board.boxSize + position % board.boxSize
                            )
                        }.toTypedArray()
                    )
                }
            }
        }

        for (polyominoId in 0 until board.getPolyominoCount()) {
            var sum: Int = 0
            var isFull = true
            for (coordinates in board.getPolyominoCoordinates(polyominoId)) {
                val value: Int? = board.getValue(coordinates.first, coordinates.second)
                if (value != null) {
                    sum += value
                } else {
                    isFull = false
                }
            }
            if ((!isFull && sum >= board.getSum(polyominoId)) ||
                (isFull && sum != board.getSum(polyominoId))
            ) {
                return KillerSudokuMistake.IncorrectSum(
                    *board.getPolyominoCoordinates(polyominoId).toTypedArray()
                )
            }
        }

        for (polyominoId in 0 until board.getPolyominoCount()) {
            val coordinates = board.getPolyominoCoordinates(polyominoId)
            for (value in board.values) {
                val valuePositions =
                    coordinates.filter { board.getValue(it.first, it.second) == value }
                if (valuePositions.size > 1) {
                    return KillerSudokuMistake.OnePolyomino(*valuePositions.toTypedArray())
                }
            }
        }

        return null
    }
}