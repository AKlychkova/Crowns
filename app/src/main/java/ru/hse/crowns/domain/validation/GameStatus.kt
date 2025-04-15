package ru.hse.crowns.domain.validation

sealed class GameStatus {
    data object Win : GameStatus()
    data object Neutral : GameStatus()
}