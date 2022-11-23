package dev.pinkroom.peanuts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun onShowButtonClicked() {
        _state.update(showHelloWorld = true)
    }

    fun onHideButtonClicked() {
        _state.update(showHelloWorld = false)
    }

    fun onLoadingClicked() = viewModelScope.launch {
        _state.update(isLoading = true)
        delay(1000)
        _state.update(isLoading = false)
    }
}