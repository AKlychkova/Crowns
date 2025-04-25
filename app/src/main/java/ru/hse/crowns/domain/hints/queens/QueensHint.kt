package ru.hse.crowns.domain.hints.queens

sealed class QueensHint() {
    class MissingCrosses(val positions: Iterable<Pair<Int, Int>>) : QueensHint()
    class OneEmptyInRow(val empty: Pair<Int, Int>, val rowCells: Iterable<Pair<Int, Int>>) :
        QueensHint()

    class OneEmptyInColumn(val empty: Pair<Int, Int>, val columnCells: Iterable<Pair<Int, Int>>) :
        QueensHint()

    class OneEmptyInPolyomino(
        val empty: Pair<Int, Int>,
        val polyominoCells: Iterable<Pair<Int, Int>>
    ) : QueensHint()

    class RuleBreakingPlacement(
        val position: Pair<Int, Int>,
        val ruleBreakingPositions: Iterable<Pair<Int, Int>>
    ) : QueensHint()

    class ExclusionZone(
        val zone: Iterable<Pair<Int, Int>>,
        val exclusion: Iterable<Pair<Int, Int>>,
        val queensAmount: Int
    ) : QueensHint()

    data object Undefined : QueensHint()
}