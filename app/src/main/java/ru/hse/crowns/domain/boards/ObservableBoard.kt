package ru.hse.crowns.domain.boards

interface ObservableBoard {
    fun addObserver(observer: BoardObserver)
    fun removeObserver(observer: BoardObserver)
    fun clearObservers()
    fun notifyObservers(row: Int, column: Int)
}