package ru.hse.crowns.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
import ru.hse.crowns.ui.game.killerSudokuGame.KillerSudokuGameViewModel
import ru.hse.crowns.ui.game.nQueensGame.NQueensGameViewModel
import ru.hse.crowns.ui.game.queensGame.QueensGameViewModel
import ru.hse.crowns.ui.home.killerSudokuHome.KillerSudokuHomeViewModel
import ru.hse.crowns.ui.home.nQueensHome.NQueensHomeViewModel
import ru.hse.crowns.ui.home.queensHome.QueensHomeViewModel
import ru.hse.crowns.ui.host.HostViewModel

val viewModelsModule = module {
    viewModel {
        KillerSudokuGameViewModel(
            boardGenerator = get(named<KillerSudokuBoard>()),
            boardValidator = get(),
            balanceRepository = get(),
            gameDataMapper = get(),
            hintsProvider = get()
        )
    }
    viewModel {
        NQueensGameViewModel(
            boardGenerator = get(named<NQueensBoard>()),
            boardValidator = get(),
            balanceRepository = get(),
            gameDataMapper = get(),
            hintsProvider = get()
        )
    }
    viewModel {
        QueensGameViewModel(
            boardGenerator = get((named<QueensBoard>())),
            boardValidator = get(),
            balanceRepository = get(),
            gameDataMapper = get(),
            hintsProvider = get()
        )
    }
    viewModel { KillerSudokuHomeViewModel(get()) }
    viewModel { NQueensHomeViewModel(get()) }
    viewModel { QueensHomeViewModel(get()) }
    viewModel { HostViewModel(get()) }
}