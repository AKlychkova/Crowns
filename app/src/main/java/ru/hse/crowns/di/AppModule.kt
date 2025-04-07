package ru.hse.crowns.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.domain.boards.NQueensBoard
import ru.hse.crowns.domain.boards.QueensBoard
import ru.hse.crowns.domain.generation.Generator
import ru.hse.crowns.domain.generation.dancingLinks.KillerSudokuDLUniqueChecker
import ru.hse.crowns.domain.generation.dancingLinks.NQueensDLUniqueChecker
import ru.hse.crowns.domain.generation.dancingLinks.QueensDLUniqueChecker
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuBacktrackingSolutionGenerator
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuGenerator
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuSolutionGenerator
import ru.hse.crowns.domain.generation.killerSudoku.KillerSudokuUniqueChecker
import ru.hse.crowns.domain.generation.nQueens.NQueensBacktrackingSolutionGenerator
import ru.hse.crowns.domain.generation.nQueens.NQueensGenerator
import ru.hse.crowns.domain.generation.nQueens.NQueensSolutionGenerator
import ru.hse.crowns.domain.generation.nQueens.NQueensUniqueChecker
import ru.hse.crowns.domain.generation.queens.QueensBacktrackingSolutionGenerator
import ru.hse.crowns.domain.generation.queens.QueensGenerator
import ru.hse.crowns.domain.generation.queens.QueensSolutionGenerator
import ru.hse.crowns.domain.generation.queens.QueensUniqueChecker
import ru.hse.crowns.ui.game.killerSudokuGame.KillerSudokuGameViewModel
import ru.hse.crowns.ui.game.nQueensGame.NQueensGameViewModel
import ru.hse.crowns.ui.game.queensGame.QueensGameViewModel
import ru.hse.crowns.ui.home.killerSudokuHome.KillerSudokuHomeViewModel
import ru.hse.crowns.ui.home.nQueensHome.NQueensHomeViewModel
import ru.hse.crowns.ui.home.queensHome.QueensHomeViewModel

val appModule = module {
    single<KillerSudokuUniqueChecker> { KillerSudokuDLUniqueChecker() }
    single<KillerSudokuSolutionGenerator> { KillerSudokuBacktrackingSolutionGenerator() }
    factory<Generator<KillerSudokuBoard>>(named<KillerSudokuBoard>()) { (maxToClear: Int) ->
        KillerSudokuGenerator(
            maxToClear,
            get(),
            get()
        )
    }

    single<NQueensUniqueChecker> { NQueensDLUniqueChecker() }
    single<NQueensSolutionGenerator> { NQueensBacktrackingSolutionGenerator() }
    factory<Generator<NQueensBoard>>(named<NQueensBoard>()) { (boardSize: Int) ->
        NQueensGenerator(
            boardSize,
            get(),
            get()
        )
    }

    single<QueensUniqueChecker> { QueensDLUniqueChecker() }
    single<QueensSolutionGenerator> { QueensBacktrackingSolutionGenerator() }
    factory<Generator<QueensBoard>>(named<QueensBoard>()) { (boardSize: Int) ->
        QueensGenerator(
            boardSize,
            get(),
            get()
        )
    }

    viewModel { (maxToClear: Int) ->
        KillerSudokuGameViewModel(get(named<KillerSudokuBoard>()) {
            parametersOf(
                maxToClear
            )
        })
    }
    viewModel { (boardSize: Int) ->
        NQueensGameViewModel(get(named<NQueensBoard>()) {
            parametersOf(
                boardSize
            )
        })
    }
    viewModel { (boardSize: Int) ->
        QueensGameViewModel(get((named<QueensBoard>())) {
            parametersOf(
                boardSize
            )
        })
    }
    viewModel { KillerSudokuHomeViewModel() }
    viewModel { NQueensHomeViewModel() }
    viewModel { QueensHomeViewModel() }
}