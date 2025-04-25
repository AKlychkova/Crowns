package ru.hse.crowns.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class BalanceRepository(private val dataStore: DataStore<Preferences>) {
    private val COINS_BALANCE = intPreferencesKey("balance")
    private val TAG: String = "BalanceRepo"

    val coinsBalanceFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[COINS_BALANCE] ?: 10
        }

    suspend fun increaseCoinsBalance(increase: Int) {
        dataStore.edit { balance ->
            val currentBalance = balance[COINS_BALANCE] ?: 10
            balance[COINS_BALANCE] = currentBalance + increase
        }
    }

    suspend fun decreaseCoinsBalance(decrease: Int) {
        dataStore.edit { balance ->
            val currentBalance = balance[COINS_BALANCE] ?: 10
            if (currentBalance >= decrease) {
                balance[COINS_BALANCE] = currentBalance - decrease
            } else {
                throw IllegalArgumentException()
            }
        }
    }

}