package dev.alibagherifam.kavoshgar.demo.lobby

import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListPresenter
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient

internal fun provideLobbyListViewModel() = LobbyListPresenter(provideClient())

private fun provideClient() = KavoshgarClient()
