package dev.pinkroom.peanuts

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun onShowButtonClicked() {
        _state.update(showHelloWorld = true)
    }

    fun onHideButtonClicked() {
        _state.update(showHelloWorld = false)
    }

    fun onShowMessageClicked() {
        _state.update(message = Message("Hello!"))
    }
}