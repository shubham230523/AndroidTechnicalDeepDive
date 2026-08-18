package com.shubhamthorat.androidtechnicaldeepdive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * ANDROID JETPACK COMPOSE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Jetpack Compose is Android's modern toolkit for building native UI. 
 * It simplifies and accelerates UI development with a declarative approach.
 *
 * KEY CONCEPTS:
 * 1. Declarative vs Imperative: You describe WHAT the UI should look like for a given state, 
 *    not HOW to change the UI (no more findViewById or manual updates).
 * 2. Composition: Building the UI tree.
 * 3. Recomposition: Re-running composables when state changes.
 */

// =========================================================================================
// PART 1: STATE MANAGEMENT (THE HEART OF COMPOSE)
// =========================================================================================

/**
 * remember: Stores a value in the Composition. It is forgotten during Recomposition 
 * unless it is 'remembered'.
 * mutableStateOf: A state holder that triggers Recomposition when its value changes.
 */
@Composable
fun StateDemo() {
    // rememberSaveable: Survives configuration changes (like rotation).
    var count by rememberSaveable { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}

/**
 * CONCEPT: State Hoisting
 * Moving state to a caller to make a composable stateless and reusable.
 * UDF (Unidirectional Data Flow): State flows down, events flow up.
 */
@Composable
fun StatelessCounter(count: Int, onIncrement: () -> Unit) {
    Column {
        Text("Stateless Count: $count")
        Button(onClick = onIncrement) {
            Text("Add")
        }
    }
}

// =========================================================================================
// PART 2: SIDE EFFECTS (HANDLING NON-UI WORK)
// =========================================================================================

/**
 * Side-effects are necessary for things like API calls, timers, or manual logging.
 * 1. LaunchedEffect: Runs in a coroutine scope when entering the composition.
 * 2. DisposableEffect: Used for cleanup (e.g. unregistering listeners).
 * 3. SideEffect: Runs after every successful recomposition.
 */
@Composable
fun EffectDemo(userId: String) {
    var userData by remember { mutableStateOf("Loading...") }

    // LaunchedEffect restarts if the 'key' (userId) changes.
    LaunchedEffect(key1 = userId) {
        delay(2000) // Simulate network call
        userData = "Data for user $userId"
    }

    Text(text = userData)
    
    // DisposableEffect example (Conceptual)
    DisposableEffect(Unit) {
        println("Init Listener")
        onDispose {
            println("Cleanup Listener")
        }
    }
}

// =========================================================================================
// PART 3: PERFORMANCE & OPTIMIZATION
// =========================================================================================

/**
 * derivedStateOf: Used when a state is derived from other states that change frequently.
 * It prevents unnecessary recompositions by only triggering when the RESULT changes.
 */
@Composable
fun OptimizedList(items: List<String>) {
    val listState = remember { mutableStateOf(items) }
    
    // Only recomposes if the Boolean condition changes, 
    // not every time the list scrolls or changes.
    val showButton by remember {
        derivedStateOf { listState.value.size > 5 }
    }

    LazyColumn {
        items(listState.value) { item ->
            Text(item, modifier = Modifier.padding(8.dp))
        }
    }
}

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: What is Recomposition?
 * A: Recomposition is the process of calling your composable functions again when 
 *    input data changes. Compose is smart enough to only recompose components 
 *    whose data has actually changed (Smart Recomposition).
 *
 * Q: Difference between 'remember' and 'rememberSaveable'?
 * A: 'remember' keeps state during recomposition; 'rememberSaveable' keeps state 
 *    during recomposition AND configuration changes (like screen rotation).
 *
 * Q: Why is 'Stability' important in Compose?
 * A: If an object is "Stable" (@Stable or @Immutable), Compose knows it can skip 
 *    recomposition if the object hasn't changed. Unstable objects (like List) 
 *    can cause unnecessary recompositions.
 *
 * Q: What is the Phase model in Compose?
 * A: 1. Composition (What to show) -> 2. Layout (Where to show) -> 3. Drawing (How to show).
 *    Compose can skip phases for optimization (e.g. skip Composition and just re-draw).
 *
 * Q: What happens if you call a side-effect directly in a Composable?
 * A: It will run every time the function recomposes, which can lead to memory leaks, 
 *    infinite loops, or wasted resources. Always wrap them in Effect handlers.
 */
