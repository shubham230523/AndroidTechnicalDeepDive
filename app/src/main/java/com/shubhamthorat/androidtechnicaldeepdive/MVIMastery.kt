package com.shubhamthorat.androidtechnicaldeepdive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ANDROID MVI ARCHITECTURE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * MVI (Model-View-Intent) is a reactive architecture based on Unidirectional Data Flow (UDF).
 * It is highly predictable and works exceptionally well with Jetpack Compose.
 */

// =========================================================================================
// PART 1: THE STATE (The Single Source of Truth)
// =========================================================================================

/**
 * ViewState: A single data class representing the entire UI state.
 * In MVI, the state is IMMUTABLE. To change the UI, you emit a new version of the state.
 */
data class UserState(
    val isLoading: Boolean = false,
    val users: List<String> = emptyList(),
    val error: String? = null
)

// =========================================================================================
// PART 2: THE INTENT (User Actions)
// =========================================================================================

/**
 * ViewIntent: Sealed class representing every possible action the user can take.
 * The View "intends" to perform an action, which is then processed by the ViewModel.
 */
sealed class UserIntent {
    object LoadUsers : UserIntent()
    data class DeleteUser(val name: String) : UserIntent()
}

// =========================================================================================
// PART 3: THE SIDE EFFECT (One-time events)
// =========================================================================================

/**
 * ViewEffect: Represents one-time events like Navigation or showing a Toast/SnackBar.
 * These are NOT part of the UI state because they don't persist across configuration changes.
 */
sealed class UserEffect {
    data class ShowToast(val message: String) : UserEffect()
    object NavigateToDetails : UserEffect()
}

// =========================================================================================
// PART 4: THE VIEWMODEL (The State Machine)
// =========================================================================================

class UserViewModel @Inject constructor() : ViewModel() {

    /**
     * 1. State: Use StateFlow to hold the immutable UI state.
     */
    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    /**
     * 2. Intent: Use a Channel to receive user intents.
     */
    val userIntent = Channel<UserIntent>(Channel.UNLIMITED)

    /**
     * 3. Effect: Use a Channel to emit one-time effects.
     */
    private val _effect = Channel<UserEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is UserIntent.LoadUsers -> fetchUsers()
                    is UserIntent.DeleteUser -> deleteUser(intent.name)
                }
            }
        }
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            // Update state: Loading
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                delay(1000) // Simulating API call
                val mockUsers = listOf("Alice", "Bob", "Charlie")
                
                // Update state: Success
                _state.value = _state.value.copy(isLoading = false, users = mockUsers)
                
                // Emit side effect
                _effect.send(UserEffect.ShowToast("Users loaded!"))
            } catch (e: Exception) {
                // Update state: Error
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun deleteUser(name: String) {
        viewModelScope.launch {
            val currentUsers = _state.value.users.toMutableList()
            currentUsers.remove(name)
            
            // Update state: New list
            _state.value = _state.value.copy(users = currentUsers)
            _effect.send(UserEffect.ShowToast("$name deleted"))
        }
    }
}

// =========================================================================================
// PART 5: THE VIEW (UI Layer)
// =========================================================================================

/**
 * In MVI, the View:
 * 1. Sends Intents to the ViewModel (e.g., viewModel.userIntent.send(UserIntent.LoadUsers))
 * 2. Observes the single State (viewModel.state.collect)
 * 3. Observes Side Effects (viewModel.effect.collect)
 */

/*
@Composable
fun UserScreen(viewModel: UserViewModel) {
    val state by viewModel.state.collectAsState()

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is UserEffect.ShowToast -> showSnackbar(effect.message)
                is UserEffect.NavigateToDetails -> navigate()
            }
        }
    }

    // Render UI based on 'state'
    if (state.isLoading) CircularProgressIndicator()
    
    UserList(users = state.users) { name ->
        scope.launch { viewModel.userIntent.send(UserIntent.DeleteUser(name)) }
    }
}
*/

// =========================================================================================
// INTERVIEW TIPS:
// 1. MVI Core Pillar: Unidirectional Data Flow (UDF). Data goes one way, actions the other.
// 2. State vs Intent: State is what the UI shows; Intent is what the User wants to do.
// 3. Why MVI? No state fragmentation (one single state object), highly testable, predictable.
// 4. MVI vs MVVM: In MVVM, ViewModels have many variables for state. In MVI, there is only ONE.
// =========================================================================================
