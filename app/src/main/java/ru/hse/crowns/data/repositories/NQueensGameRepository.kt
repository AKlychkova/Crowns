package ru.hse.crowns.data.repositories

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.firstOrNull
import ru.hse.crowns.proto.NQueensGameDTO

class NQueensGameRepository(private val dataStore: DataStore<NQueensGameDTO?>) {
    suspend fun getData() : NQueensGameDTO? = dataStore.data.firstOrNull()

    suspend fun updateData(data : NQueensGameDTO) {
        dataStore.updateData { data }
    }

    suspend fun removeData() {
        dataStore.updateData { null }
    }
}