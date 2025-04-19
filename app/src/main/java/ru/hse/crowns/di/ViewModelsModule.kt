package ru.hse.crowns.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.domain.boards.NQueensBoard
import ru.hse.crowns.domain.boards.QueensBoard
import ru.hse.crowns.ui.game.killerSudokuGame.KillerSudokuGameViewModel
import ru.hse.crowns.ui.game.nQueensGame.NQueensGameViewModel
import ru.hse.crowns.ui.game.queensGame.QueensGameViewModel
import ru.hse.crowns.ui.home.killerSudokuHome.KillerSudokuHomeViewModel
import ru.hse.crowns.ui.home.nQueensHome.NQueensHomeViewModel
import ru.hse.crowns.ui.home.queensHome.QueensHomeViewModel
import ru.hse.crowns.ui.host.HostViewModel

val viewModelsModule = module {
    viewModel { (maxToClear: Int) ->
        KillerSudokuGameViewModel(
            boardGenerator = get(named<KillerSudokuBoard>()) {
                parametersOf(
                    maxToClear
                )
            },
            boardValidator = get(),
            balanceRepository = get(),
            gameDataMapper = get()
        )
    }
    viewModel { (boardSize: Int) ->
        NQueensGameViewModel(
            boardGenerator = get(named<NQueensBoard>()) {
                parametersOf(
                    boardSize
                )
            },
            boardValidator = get(),
            balanceRepository = get(),
            gameDataMapper = get()
        )
    }
    viewModel { (boardSize: Int) ->
        QueensGameViewModel(
            boardGenerator = get((named<QueensBoard>())) {
                parametersOf(
                    boardSize
                )
            },
            boardValidator = get(),
            balanceRepository = get(),
            gameDataMapper = get()
        )
    }
    viewModel { KillerSudokuHomeViewModel(get()) }
    viewModel { NQueensHomeViewModel(get()) }
    viewModel { QueensHomeViewModel(get()) }
    viewModel { HostViewModel(get()) }
}