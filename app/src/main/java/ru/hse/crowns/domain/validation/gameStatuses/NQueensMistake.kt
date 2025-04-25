package ru.hse.crowns.domain.validation.gameStatuses

sealed class NQueensMistake(val positions: Iterable<Pair<Int, Int>>) : GameStatus() {
    abstract fun getMessage() : String

    class OneDiagonal(queenPositions: Iterable<Pair<Int, Int>>) : NQueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "Выделенные ферзи стоят на одной дигонали."
        }
    }

    class OneRow(queenPositions: Iterable<Pair<Int, Int>>) : NQueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "Выделенные ферзи стоят в одной строке."
        }
    }

    class OneColumn(queenPositions: Iterable<Pair<Int, Int>>) : NQueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "Выделенные ферзи стоят в одном столбце."
        }
    }

    class EmptyRow(queenPositions: Iterable<Pair<Int, Int>>) : NQueensMistake(queenPositions)  {
        override fun getMessage(): String {
            return "В выделенной строке нет ферзей."
        }
    }

    class EmptyColumn(queenPositions: Iterable<Pair<Int, Int>>) : NQueensMistake(queenPositions)  {
        override fun getMessage(): String {
            return "В выделенном столбце нет ферзей."
        }
    }
}