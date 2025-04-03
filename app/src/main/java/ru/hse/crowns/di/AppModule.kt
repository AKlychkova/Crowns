package ru.hse.crowns.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.domain.generation.Generator
import ru.hse.crowns.domain.generation.dancingLinks.KillerSudokuDLUniqueChecker
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuBacktrackingSolutionGenerator
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuGenerator
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuSolutionGenerator
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuUniqueChecker
import ru.hse.crowns.ui.game.killerSudokuGame.KillerSudokuGameViewModel
import ru.hse.crowns.ui.home.killerSudokuHome.KillerSudokuHomeViewModel
import ru.hse.crowns.ui.home.nQueensHome.NQueensHomeViewModel
import ru.hse.crowns.ui.home.queensHome.QueensHomeViewModel

val appModule = module {
    single<KillerSudokuUniqueChecker> { KillerSudokuDLUniqueChecker() }
    single<KillerSudokuSolutionGenerator> { KillerSudokuBacktrackingSolutionGenerator() }
    single<Generator<KillerSudokuBoard>> { (maxToClear: Int) ->
        KillerSudokuGenerator(
            maxToClear,
            get(),
            get()
        )
    }

    viewModel { (maxToClear: Int) -> KillerSudokuGameViewModel(get { parametersOf(maxToClear) }) }
    viewModel { KillerSudokuHomeViewModel() }
    viewModel { NQueensHomeViewModel() }
    viewModel { QueensHomeViewModel() }
}