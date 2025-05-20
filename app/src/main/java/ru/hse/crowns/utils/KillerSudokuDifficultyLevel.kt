package ru.hse.crowns.utils

enum class KillerSudokuDifficultyLevel {
    EASY {
        override fun getMaxToDelete(): Int = 30
    },
    MEDIUM {
        override fun getMaxToDelete(): Int = 50
    },
    DIFFICULT {
        override fun getMaxToDelete(): Int = 60
    };

    abstract fun getMaxToDelete() : Int
}