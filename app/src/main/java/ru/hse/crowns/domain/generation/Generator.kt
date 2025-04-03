package ru.hse.crowns.domain.generation

interface Generator<T> {
    fun generate() : T
}