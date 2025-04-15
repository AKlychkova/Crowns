package ru.hse.crowns.domain.validation

sealed class QueensMistake(vararg val queenPositions: Pair<Int, Int>) : GameStatus() {
    abstract fun getMessage() : String

    class OneRow(vararg queenPositions: Pair<Int, Int>) : QueensMistake(*queenPositions) {
        override fun getMessage(): String {
            return "Выделенные королевы стоят в одной строке."
        }
    }

    class OneColumn(vararg queenPositions: Pair<Int, Int>) : QueensMistake(*queenPositions) {
        override fun getMessage(): String {
            return "Выделенные королевы стоят в одном столбце."
        }
    }

    class Touch(queen1: Pair<Int, Int>, queen2 : Pair<Int, Int>) : QueensMistake(queen1, queen2) {
        override fun getMessage(): String {
            return "Выделенные королевы касаются друг друга."
        }
    }

    class OnePolyomino(vararg queenPositions: Pair<Int, Int>) : QueensMistake(*queenPositions) {
        override fun getMessage(): String {
            return "Выделенные королевы стоят в одном полимино."
        }
    }
}