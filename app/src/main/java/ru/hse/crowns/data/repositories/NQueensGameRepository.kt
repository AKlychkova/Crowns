package ru.hse.crowns.data.repositories

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.hse.crowns.proto.NQueensGameDTO

class NQueensGameRepository(private val dataStore: DataStore<NQueensGameDTO>) {
    val gameFlow: Flow<NQueensGameDTO?> = dataStore.data.map { data ->
        if (data == NQueensGameDTO.getDefaultInstance())
            null
        else
            data
    }

    /**
     * @return [NQueensGameDTO] with data from DataStore
     */
    suspend fun getData(): NQueensGameDTO? = gameFlow.firstOrNull()

    /**
     * Update data in DataStore
     */
    suspend fun updateData(data: NQueensGameDTO) {
        dataStore.updateData { data }
    }

    /**
     * Clear data in DataStore
     */
    suspend fun removeData() {
        dataStore.updateData { NQueensGameDTO.getDefaultInstance() }
    }
}