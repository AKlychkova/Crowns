package ru.hse.crowns.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.hse.crowns.data.repositories.BalanceRepository
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
import ru.hse.crowns.domain.validation.KillerSudokuValidator
import ru.hse.crowns.domain.validation.KillerSudokuValidatorImpl
import ru.hse.crowns.domain.validation.NQueensValidator
import ru.hse.crowns.domain.validation.NQueensValidatorImpl
import ru.hse.crowns.domain.validation.QueensValidator
import ru.hse.crowns.domain.validation.QueensValidatorImpl

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
    single<KillerSudokuValidator> { KillerSudokuValidatorImpl() }

    single<NQueensUniqueChecker> { NQueensDLUniqueChecker() }
    single<NQueensSolutionGenerator> { NQueensBacktrackingSolutionGenerator() }
    factory<Generator<NQueensBoard>>(named<NQueensBoard>()) { (boardSize: Int) ->
        NQueensGenerator(
            boardSize,
            get(),
            get()
        )
    }
    single<NQueensValidator> { NQueensValidatorImpl() }

    single<QueensUniqueChecker> { QueensDLUniqueChecker() }
    single<QueensSolutionGenerator> { QueensBacktrackingSolutionGenerator() }
    factory<Generator<QueensBoard>>(named<QueensBoard>()) { (boardSize: Int) ->
        QueensGenerator(
            boardSize,
            get(),
            get()
        )
    }
    single<QueensValidator> { QueensValidatorImpl() }

    single<BalanceRepository> { BalanceRepository(get()) }
}