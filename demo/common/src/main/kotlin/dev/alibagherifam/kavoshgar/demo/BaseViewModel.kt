package dev.alibagherifam.kavoshgar.demo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

abstract class BaseViewModel<State>(
    viewModelScope: CoroutineScope,
    initialState: State
) {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> get() = _uiState

    protected val safeScope = viewModelScope

    protected inline fun launchInUi(crossinline block: suspend () -> Unit) {
        safeScope.launch {
            block()
        }
    }

    protected fun Flow<*>.launchInUi() {
        launchIn(safeScope)
    }
}
