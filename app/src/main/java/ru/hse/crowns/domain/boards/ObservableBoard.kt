package ru.hse.crowns.domain.boards

interface ObservableBoard {
    /**
     * Add [observer]
     */
    fun addObserver(observer: BoardObserver)

    /**
     * Remove the first occurrence of the [observer], if it is present.
     */
    fun removeObserver(observer: BoardObserver)

    /**
     * Remove all observers
     */
    fun clearObservers()

    /**
     * Call [onChanged][BoardObserver.onChanged] function for all observers
     */
    fun notifyObservers(row: Int, column: Int)
}