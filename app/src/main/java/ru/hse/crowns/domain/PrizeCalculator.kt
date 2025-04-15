package ru.hse.crowns.domain

import kotlin.math.max

class PrizeCalculator {
    companion object {
        fun calculate(time: Int, level: Double, hintCount: Int, mistakeCount: Int): Int {
            return max(1, 5 + (level - 0.1 * time).toInt() - mistakeCount - hintCount)
        }
    }
}