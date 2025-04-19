package ru.hse.crowns.ui.home.nQueensHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.crowns.domain.mappers.NQueensMapper

class NQueensHomeViewModel(private val gameDataMapper: NQueensMapper) : ViewModel() {

    private val _hasGameData = MutableLiveData<Boolean>()
    val hasGameData: LiveData<Boolean> = _hasGameData

    var dataBoardSize: Int? = null
        private set

    fun checkGameData() = viewModelScope.launch(Dispatchers.IO) {
        val hasData = gameDataMapper.hasData()
        launch(Dispatchers.Main) {
            _hasGameData.value = hasData
        }
        if (hasData) {
            val size = gameDataMapper.getBoardSize()
            launch(Dispatchers.Main) {
                dataBoardSize = size
            }
        }
    }
}