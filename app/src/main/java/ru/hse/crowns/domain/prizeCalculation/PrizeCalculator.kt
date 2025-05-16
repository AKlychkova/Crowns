package ru.hse.crowns.domain.prizeCalculation

import kotlin.math.max

class PrizeCalculator {
    companion object {
        /**
         * Calculate prize depend on parameters
         * @param time solution time
         * @param level difficulty
         * @param hintCount the number of taken hints
         * @param mistakeCount the number of mistakes
         * @return calculated prize
         */
        fun calculate(time: Int, level: Double, hintCount: Int, mistakeCount: Int): Int {
            return max(1, 5 + (level - 0.1 * time).toInt() - mistakeCount - hintCount)
        }
    }
}