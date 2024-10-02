package dev.alibagherifam.kavoshgar.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

abstract class Presenter<State : Any, Event : Any> : ViewModel() {
    abstract val uiState: StateFlow<State>

    abstract val eventSink: (Event) -> Unit

    protected val presenterScope: CoroutineScope
        get() = viewModelScope
}
