package ru.hse.crowns.ui.home.killerSudokuHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.hse.crowns.domain.mappers.KillerSudokuMapper

class KillerSudokuHomeViewModel (private val gameDataMapper: KillerSudokuMapper) : ViewModel() {
    val hasGameData: LiveData<Boolean> = gameDataMapper.hasDataFlow.asLiveData()
}