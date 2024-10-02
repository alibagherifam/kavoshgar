package dev.alibagherifam.kavoshgar.demo.lobby

import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListViewModel
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient

internal fun provideLobbyListViewModel() = LobbyListViewModel(provideClient())

private fun provideClient() = KavoshgarClient()
