# Peanuts

If you use Kotlin flows to manage your Jetpack Compose UI states, chances are that this code is
familiar to you on a daily basis.

You have a state:

```kotlin
data class MyScreenState(
    val isLoading: Boolean = false,
    // ...
)
```

And, in your ViewModel, you init and expose that state:

```kotlin
class MyScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(MyScreenState())
    val state = _state.asStateFlow()
}
```

Then, whenever you want to update the screen state, you need to do something like:

```kotlin
_state.update { currentState -> currentState.copy(isLoading = true) }
```

-------
<p align="center">
    <a href="#why">Why?</a> &bull;
    <a href="#installation">Installation</a> &bull;
    <a href="#usage">Usage</a>
</p>

-------

## Why Peanuts?

Even tho the code above is simple, it has 2 main flaws:

1. You always need to remember to copy the current state to create a new state;
2. Due to that, you need to write the same code over and over again.

Peanuts is a very simple library that uses KSP and KotlinPoet to simplify the screen state updates.
It will automatically generate an extension function for you, like this one:

````kotlin
fun MutableStateFlow<MyScreenState>.update(isLoading: Boolean = value.isLoading, ...) =
    update { it.copy(isLoading = isLoading, ...) }
````

Allowing you to update your screen state simply with:

````kotlin
_state.update(isLoading = true)
````

## Installation

``` groovy
implementation 'dev.pinkroom:peanuts:<latest_version>'
```

## Usage

First, you need to annotate your screen states with `@PeanutState`:

```kotlin
@PeanutState
data class MyScreenState(
    val isLoading: Boolean = false,
    // ...
)
```

After your code compiles, updating your screen state is peanuts:

````kotlin
_state.update(isLoading = true)
````

## License

    Copyright 2022 Pink Room, Lda

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.