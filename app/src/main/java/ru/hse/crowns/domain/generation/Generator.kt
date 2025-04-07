package ru.hse.crowns.domain.generation

interface Generator<T> {
    suspend fun generate() : T
}