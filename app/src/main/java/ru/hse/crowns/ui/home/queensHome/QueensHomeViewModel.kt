package ru.hse.crowns.ui.home.queensHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.hse.crowns.domain.mappers.QueensMapper

class QueensHomeViewModel(private val gameDataMapper: QueensMapper) : ViewModel() {

    val hasGameData: LiveData<Boolean> = gameDataMapper.hasDataFlow.asLiveData()

}