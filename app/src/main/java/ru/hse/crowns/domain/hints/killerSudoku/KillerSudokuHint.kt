package ru.hse.crowns.domain.hints.killerSudoku

sealed class KillerSudokuHint() {
    class OneEmptyInRow(
        val empty: Pair<Int, Int>,
        val rowCells: Iterable<Pair<Int, Int>>,
        val value: Int
    ) : KillerSudokuHint()

    class OneEmptyInColumn(
        val empty: Pair<Int, Int>,
        val columnCells: Iterable<Pair<Int, Int>>,
        val value: Int
    ) : KillerSudokuHint()

    class OneEmptyInPolyomino(
        val empty: Pair<Int, Int>,
        val polyominoCells: Iterable<Pair<Int, Int>>,
        val value: Int
    ) : KillerSudokuHint()

    class OneEmptyInBox(
        val empty: Pair<Int, Int>,
        val boxCells: Iterable<Pair<Int, Int>>,
        val value: Int
    ) : KillerSudokuHint()

    data object Undefined : KillerSudokuHint()
}