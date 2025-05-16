package ru.hse.crowns.utils

/**
 * @return max of the elements
 */
fun Pair<Int, Int>.max() = kotlin.math.max(this.first, this.second)

/**
 * @return min of the elements
 */
fun Pair<Int, Int>.min() = kotlin.math.min(this.first, this.second)

/**
 * Sum element by element
 */
operator fun Pair<Int, Int>.plus(number: Int): Pair<Int, Int> {
    return Pair(this.first + number, this.second + number)
}

/**
 * @return List of all subsets
 */
fun <T> Set<T>.getPowerSet(): List<Set<T>> {
    val n = this.size
    val powSetSize = 1 shl n
    val powerSet = mutableListOf<Set<T>>()

    for (i in 0 until powSetSize) {
        val subset = mutableSetOf<T>()
        for (j in 0 until n) {
            if (i and (1 shl j) != 0) {
                subset.add(this.elementAt(j))
            }
        }
        powerSet.add(subset)
    }
    return powerSet
}