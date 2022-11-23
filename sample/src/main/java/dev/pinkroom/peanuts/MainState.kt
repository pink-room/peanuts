package dev.pinkroom.peanuts

@PeanutState
data class MainState(
    val isLoading: Boolean = false,
    val showHelloWorld: Boolean = false,
)