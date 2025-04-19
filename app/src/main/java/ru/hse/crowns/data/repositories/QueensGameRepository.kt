package ru.hse.crowns.data.repositories

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.firstOrNull
import ru.hse.crowns.proto.QueensGameDTO

class QueensGameRepository (private val dataStore: DataStore<QueensGameDTO?>) {
    suspend fun getData() : QueensGameDTO? = dataStore.data.firstOrNull()

    suspend fun updateData(data : QueensGameDTO) {
        dataStore.updateData { data }
    }

    suspend fun removeData() {
        dataStore.updateData { null }
    }
}