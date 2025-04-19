package ru.hse.crowns.ui.home.killerSudokuHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.crowns.domain.mappers.KillerSudokuMapper

class KillerSudokuHomeViewModel (private val gameDataMapper: KillerSudokuMapper) : ViewModel() {
    private val _hasGameData = MutableLiveData<Boolean>()
    val hasGameData: LiveData<Boolean> = _hasGameData

    var dataBoardLevel: Int? = null
        private set

    fun checkGameData() = viewModelScope.launch(Dispatchers.IO) {
        val hasData = gameDataMapper.hasData()
        launch(Dispatchers.Main) {
            _hasGameData.value = hasData
        }
        if (hasData) {
            val level = gameDataMapper.getLevel()
            launch(Dispatchers.Main) {
                dataBoardLevel = level
            }
        }
    }
}