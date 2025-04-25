package ru.hse.crowns.ui.home.nQueensHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.hse.crowns.domain.mappers.NQueensMapper

class NQueensHomeViewModel(private val gameDataMapper: NQueensMapper) : ViewModel() {

    val hasGameData: LiveData<Boolean> = gameDataMapper.hasDataFlow.asLiveData()

}