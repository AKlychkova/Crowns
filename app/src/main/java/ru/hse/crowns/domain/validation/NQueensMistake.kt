package ru.hse.crowns.domain.validation

sealed class NQueensMistake(vararg val queenPositions: Pair<Int, Int>) : GameStatus() {
    abstract fun getMessage() : String

    class OneDiagonal(vararg queenPositions: Pair<Int, Int>) : NQueensMistake(*queenPositions) {
        override fun getMessage(): String {
            return "Выделенные ферзи стоят на одной дигонали."
        }
    }

    class OneRow(vararg queenPositions: Pair<Int, Int>) : NQueensMistake(*queenPositions) {
        override fun getMessage(): String {
            return "Выделенные ферзи стоят в одной строке."
        }
    }

    class OneColumn(vararg queenPositions: Pair<Int, Int>) : NQueensMistake(*queenPositions) {
        override fun getMessage(): String {
            return "Выделенные ферзи стоят в одном столбце."
        }
    }
}