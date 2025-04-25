package ru.hse.crowns.domain.generation

interface Generator<in I, out O> {
    suspend fun generate(input: I) : O
}