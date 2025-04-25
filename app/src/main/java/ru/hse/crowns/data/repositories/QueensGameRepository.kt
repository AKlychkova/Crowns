package ru.hse.crowns.data.repositories

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.hse.crowns.proto.QueensGameDTO

class QueensGameRepository (private val dataStore: DataStore<QueensGameDTO>) {
    val gameFlow : Flow<QueensGameDTO?> = dataStore.data.map { data ->
        if (data == QueensGameDTO.getDefaultInstance())
            null
        else
            data
    }

    suspend fun getData() : QueensGameDTO? = gameFlow.firstOrNull()

    suspend fun updateData(data : QueensGameDTO) {
        dataStore.updateData { data }
    }

    suspend fun removeData() {
        dataStore.updateData { QueensGameDTO.getDefaultInstance() }
    }
}