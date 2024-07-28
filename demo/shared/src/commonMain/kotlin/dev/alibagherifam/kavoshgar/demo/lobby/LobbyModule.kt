package dev.alibagherifam.kavoshgar.demo.lobby

import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListViewModel
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient
import kotlinx.coroutines.CoroutineScope

internal fun provideLobbyListViewModel(viewModelScope: CoroutineScope) =
    LobbyListViewModel(viewModelScope, provideClient())

private fun provideClient() = KavoshgarClient()
