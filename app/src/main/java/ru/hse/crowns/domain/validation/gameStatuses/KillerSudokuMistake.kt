package ru.hse.crowns.domain.validation.gameStatuses

sealed class KillerSudokuMistake(vararg val positions: Pair<Int, Int>) : GameStatus(){
    abstract fun getMessage() : String

    class OneRow(vararg positions: Pair<Int, Int>) : KillerSudokuMistake(*positions) {
        override fun getMessage(): String {
            return "Одинаковые значения в одной строке."
        }
    }

    class OneColumn(vararg positions: Pair<Int, Int>) : KillerSudokuMistake(*positions) {
        override fun getMessage(): String {
            return "Одинаковые значения в одном столбце."
        }
    }

    class OneBox(vararg positions: Pair<Int, Int>) : KillerSudokuMistake(*positions) {
        override fun getMessage(): String {
            return "Одинаковые значения в одном малом квадрате."
        }
    }

    class OnePolyomino(vararg positions: Pair<Int, Int>) : KillerSudokuMistake(*positions) {
        override fun getMessage(): String {
            return "Одинаковые значения в одной цветовой зоне."
        }
    }

    class IncorrectSum(vararg positions: Pair<Int, Int>) : KillerSudokuMistake(*positions) {
        override fun getMessage(): String {
            return "В выделенной цветовой зоне не может получиться правильная сумма."
        }
    }
}