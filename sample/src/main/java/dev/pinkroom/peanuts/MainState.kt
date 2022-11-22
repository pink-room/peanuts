package dev.pinkroom.peanuts

@PeanutState
data class MainState(
    val showHelloWorld: Boolean = false,
    val message: Message? = null,
)