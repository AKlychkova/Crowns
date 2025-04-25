package ru.hse.crowns.di

import androidx.datastore.preferences.core.Preferences
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.hse.crowns.data.repositories.BalanceRepository
import ru.hse.crowns.data.repositories.KillerSudokuGameRepository
import ru.hse.crowns.data.repositories.NQueensGameRepository
import ru.hse.crowns.data.repositories.QueensGameRepository
import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.domain.domainObjects.boards.QueensBoard
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
import ru.hse.crowns.domain.hints.killerSudoku.KillerSudokuHint
import ru.hse.crowns.domain.hints.killerSudoku.KillerSudokuHintsProvider
import ru.hse.crowns.domain.hints.killerSudoku.KillerSudokuHintsProviderImpl
import ru.hse.crowns.domain.hints.nQueens.NQueensHintsProvider
import ru.hse.crowns.domain.hints.nQueens.NQueensHintsProviderImpl
import ru.hse.crowns.domain.hints.queens.QueensHintsProvider
import ru.hse.crowns.domain.hints.queens.QueensHintsProviderImpl
import ru.hse.crowns.domain.mappers.KillerSudokuMapper
import ru.hse.crowns.domain.mappers.NQueensMapper
import ru.hse.crowns.domain.mappers.QueensMapper
import ru.hse.crowns.domain.validation.killerSudoku.KillerSudokuValidator
import ru.hse.crowns.domain.validation.killerSudoku.KillerSudokuValidatorImpl
import ru.hse.crowns.domain.validation.nQueens.NQueensValidator
import ru.hse.crowns.domain.validation.nQueens.NQueensValidatorImpl
import ru.hse.crowns.domain.validation.queens.QueensValidator
import ru.hse.crowns.domain.validation.queens.QueensValidatorImpl
import ru.hse.crowns.proto.KillerSudokuGameDTO
import ru.hse.crowns.proto.NQueensGameDTO
import ru.hse.crowns.proto.QueensGameDTO

val appModule = module {
    single<KillerSudokuUniqueChecker> { KillerSudokuDLUniqueChecker() }
    single<KillerSudokuSolutionGenerator> { KillerSudokuBacktrackingSolutionGenerator() }
    single<Generator<Int, KillerSudokuBoard>>(named<KillerSudokuBoard>()) {
        KillerSudokuGenerator(
            get(),
            get()
        )
    }
    single<KillerSudokuValidator> { KillerSudokuValidatorImpl() }

    single<NQueensUniqueChecker> { NQueensDLUniqueChecker() }
    single<NQueensSolutionGenerator> { NQueensBacktrackingSolutionGenerator() }
    single<Generator<Int, NQueensBoard>>(named<NQueensBoard>()) {
        NQueensGenerator(
            get(),
            get()
        )
    }
    single<NQueensValidator> { NQueensValidatorImpl() }

    single<QueensUniqueChecker> { QueensDLUniqueChecker() }
    single<QueensSolutionGenerator> { QueensBacktrackingSolutionGenerator() }
    single<Generator<Int, QueensBoard>>(named<QueensBoard>()) {
        QueensGenerator(
            get(),
            get()
        )
    }
    single<QueensValidator> { QueensValidatorImpl() }

    single<BalanceRepository> { BalanceRepository(get(named<Preferences>())) }
    single<NQueensGameRepository> { NQueensGameRepository(get(named<NQueensGameDTO>())) }
    single<QueensGameRepository> { QueensGameRepository(get(named<QueensGameDTO>())) }
    single<KillerSudokuGameRepository> { KillerSudokuGameRepository(get(named<KillerSudokuGameDTO>())) }

    single<NQueensMapper> { NQueensMapper(get()) }
    single<QueensMapper> { QueensMapper(get()) }
    single<KillerSudokuMapper> { KillerSudokuMapper(get()) }

    single<NQueensHintsProvider> { NQueensHintsProviderImpl(get()) }
    single<QueensHintsProvider> { QueensHintsProviderImpl(get()) }
    single<KillerSudokuHintsProvider> { KillerSudokuHintsProviderImpl() }
}