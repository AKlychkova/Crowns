package ru.hse.crowns.di

import androidx.datastore.core.DataStore
import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.hse.crowns.data.serializers.KillerSudokuGameSerializer
import ru.hse.crowns.data.serializers.NQueensGameSerializer
import ru.hse.crowns.data.serializers.QueensGameSerializer
import ru.hse.crowns.proto.KillerSudokuGameDTO
import ru.hse.crowns.proto.NQueensGameDTO
import ru.hse.crowns.proto.QueensGameDTO

private const val BALANCE_NAME = "balance"
private const val N_QUEENS_NAME = "n_queens2"
private const val QUEENS_NAME = "queens"
private const val KILLER_SUDOKU_NAME = "n_queens"

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = BALANCE_NAME)
private val Context.nQueensDataStore: DataStore<NQueensGameDTO?> by dataStore(
    fileName = N_QUEENS_NAME,
    serializer = NQueensGameSerializer
)
private val Context.queensDataStore: DataStore<QueensGameDTO?> by dataStore(
    fileName = QUEENS_NAME,
    serializer = QueensGameSerializer
)
private val Context.killerSudokuDataStore: DataStore<KillerSudokuGameDTO?> by dataStore(
    fileName = KILLER_SUDOKU_NAME,
    serializer = KillerSudokuGameSerializer
)

val dataStoreModule = module {
    single<DataStore<Preferences>> (named<Preferences>()){
        androidApplication().preferencesDataStore
    }

    single<DataStore<NQueensGameDTO?>> (named<NQueensGameDTO>()) {
        androidApplication().nQueensDataStore
    }

    single<DataStore<QueensGameDTO?>> (named<QueensGameDTO>()) {
        androidApplication().queensDataStore
    }

    single<DataStore<KillerSudokuGameDTO?>> (named<KillerSudokuGameDTO>()) {
        androidApplication().killerSudokuDataStore
    }
}
