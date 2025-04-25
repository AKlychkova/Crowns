package ru.hse.crowns.domain.validation.gameStatuses

sealed class GameStatus {
    data object Win : GameStatus()
    data object Neutral : GameStatus()
}