package ru.hse.crowns.data.repositories

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.firstOrNull
import ru.hse.crowns.proto.KillerSudokuGameDTO

class KillerSudokuGameRepository (private val dataStore: DataStore<KillerSudokuGameDTO?>) {
    suspend fun getData() : KillerSudokuGameDTO? = dataStore.data.firstOrNull()

    suspend fun updateData(data : KillerSudokuGameDTO) {
        dataStore.updateData { data }
    }

    suspend fun removeData() {
        dataStore.updateData { null }
    }
}