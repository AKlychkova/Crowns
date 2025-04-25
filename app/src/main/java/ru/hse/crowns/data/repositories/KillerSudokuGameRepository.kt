package ru.hse.crowns.data.repositories

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.hse.crowns.proto.KillerSudokuGameDTO

class KillerSudokuGameRepository (private val dataStore: DataStore<KillerSudokuGameDTO>) {
    val gameFlow : Flow<KillerSudokuGameDTO?> = dataStore.data.map { data ->
        if (data == KillerSudokuGameDTO.getDefaultInstance())
            null
        else
            data
    }

    suspend fun getData() : KillerSudokuGameDTO? = dataStore.data.firstOrNull()

    suspend fun updateData(data : KillerSudokuGameDTO) {
        dataStore.updateData { data }
    }

    suspend fun removeData() {
        dataStore.updateData { KillerSudokuGameDTO.getDefaultInstance() }
    }
}