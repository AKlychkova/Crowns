package ru.hse.crowns.domain.hints.nQueens

sealed class NQueensHint() {
    class MissingCrosses(val positions: Iterable<Pair<Int, Int>>) : NQueensHint()
    class OneEmptyInRow(val empty: Pair<Int, Int>, val rowCells: Iterable<Pair<Int, Int>>) :
        NQueensHint()

    class OneEmptyInColumn(val empty: Pair<Int, Int>, val columnCells: Iterable<Pair<Int, Int>>) :
        NQueensHint()

    class RuleBreakingPlacement(
        val position: Pair<Int, Int>,
        val ruleBreakingPositions: Iterable<Pair<Int, Int>>
    ) : NQueensHint()

    class ExclusionZone(
        val zone: Iterable<Pair<Int, Int>>,
        val exclusion: Iterable<Pair<Int, Int>>,
        val queensAmount: Int
    ) : NQueensHint()

    class Undefined() : NQueensHint()
}