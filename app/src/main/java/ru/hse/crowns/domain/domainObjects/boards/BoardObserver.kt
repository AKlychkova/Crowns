package ru.hse.crowns.domain.domainObjects.boards

interface BoardObserver {
    /**
     * Called when the cell with coordinates [row] and [column] is changed.
     */
    fun onChanged(row: Int, column: Int)
}