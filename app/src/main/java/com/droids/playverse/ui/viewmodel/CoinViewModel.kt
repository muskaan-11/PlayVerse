package com.droids.playverse.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.droids.playverse.data.ServiceLocator
import com.droids.playverse.data.repository.CoinRepository
import kotlinx.coroutines.flow.StateFlow

class CoinViewModel : ViewModel() {

    private val coinRepository: CoinRepository = ServiceLocator.provideCoinRepository()

    val balance: StateFlow<Int> = coinRepository.balance
}