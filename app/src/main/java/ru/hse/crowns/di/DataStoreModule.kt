package ru.hse.crowns.di

import androidx.datastore.core.DataStore
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val BALANCE_NAME = "balance"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BALANCE_NAME)

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        androidApplication().dataStore
    }
}
