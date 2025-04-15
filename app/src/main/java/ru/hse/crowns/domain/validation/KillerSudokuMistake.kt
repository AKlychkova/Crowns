package ru.hse.crowns.domain.validation

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
            return "Одинаковые значения в одном полимино."
        }
    }

    class IncorrectSum(vararg positions: Pair<Int, Int>) : KillerSudokuMistake(*positions) {
        override fun getMessage(): String {
            return "Неправильная сумма в выделенном полимино."
        }
    }
}