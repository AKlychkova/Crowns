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

    /**
     * @return [KillerSudokuGameDTO] with data from DataStore
     */
    suspend fun getData() : KillerSudokuGameDTO? = dataStore.data.firstOrNull()

    /**
     * Update data in DataStore
     */
    suspend fun updateData(data : KillerSudokuGameDTO) {
        dataStore.updateData { data }
    }

    /**
     * Clear data in DataStore
     */
    suspend fun removeData() {
        dataStore.updateData { KillerSudokuGameDTO.getDefaultInstance() }
    }
}