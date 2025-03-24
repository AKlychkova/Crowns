package ru.hse.crowns.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.hse.crowns.ui.game.GameViewModel
import ru.hse.crowns.ui.killerSudokuHome.KillerSudokuHomeViewModel
import ru.hse.crowns.ui.nQueensHome.NQueensHomeViewModel
import ru.hse.crowns.ui.queensHome.QueensHomeViewModel

val appModule = module {
    viewModel { GameViewModel() }
    viewModel { KillerSudokuHomeViewModel() }
    viewModel { NQueensHomeViewModel() }
    viewModel { QueensHomeViewModel() }
}