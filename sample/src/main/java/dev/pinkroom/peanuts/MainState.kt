package dev.pinkroom.peanuts

@PeanutState
data class MainState(
    val test: Test? = null,
    val show: Boolean = false,
    val message: String? = null,
)