package ru.hse.crowns.domain.validation.gameStatuses

sealed class QueensMistake(val positions: Iterable<Pair<Int, Int>>) : GameStatus() {
    abstract fun getMessage() : String

    class OneRow(queenPositions: Iterable<Pair<Int, Int>>) : QueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "Выделенные королевы стоят в одной строке."
        }
    }

    class OneColumn(queenPositions: Iterable<Pair<Int, Int>>) : QueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "Выделенные королевы стоят в одном столбце."
        }
    }

    class Touch(queen1: Pair<Int, Int>, queen2 : Pair<Int, Int>) : QueensMistake(listOf(queen1, queen2)) {
        override fun getMessage(): String {
            return "Выделенные королевы касаются друг друга."
        }
    }

    class OnePolyomino(queenPositions: Iterable<Pair<Int, Int>>) : QueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "Выделенные королевы стоят в одном полимино."
        }
    }

    class EmptyRow(queenPositions: Iterable<Pair<Int, Int>>) : QueensMistake(queenPositions)  {
        override fun getMessage(): String {
            return "В выделенной строке нет королев."
        }
    }

    class EmptyColumn(queenPositions: Iterable<Pair<Int, Int>>) : QueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "В выделенном столбце нет королев."
        }
    }

    class EmptyPolyomino(queenPositions: Iterable<Pair<Int, Int>>) : QueensMistake(queenPositions) {
        override fun getMessage(): String {
            return "В выделенном полимино нет королев."
        }
    }
}