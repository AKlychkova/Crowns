package ru.hse.crowns.ui.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.hse.crowns.data.repositories.BalanceRepository

class HostViewModel(private val balanceRepository: BalanceRepository) : ViewModel() {
    val coinsBalance: LiveData<Int> = balanceRepository.coinsBalanceFlow.asLiveData()
}