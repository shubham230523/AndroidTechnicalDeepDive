package com.shubhamthorat.androidtechnicaldeepdive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * COMPOSE PERFORMANCE & RECOMPOSITION MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Performance in Compose is largely about managing Recomposition. Unnecessary 
 * recompositions lead to dropped frames, battery drain, and laggy UI.
 *
 * KEY CONCEPTS:
 * 1. Stability: Determining if a Composable can be skipped.
 * 2. derivedStateOf: Buffering high-frequency state changes.
 * 3. Phase Model: Deferring state reads to Layout/Draw phases.
 * 4. Lazy List Optimization: Efficiently updating large collections.
 */

// =========================================================================================
// PART 1: STABILITY & IMMUTABILITY (@Stable & @Immutable)
// =========================================================================================

/**
 * A Composable is "Skippable" if all its parameters are stable.
 * - Primitive types (Int, String, etc.) are stable.
 * - Standard Collections (List, Map) are UNSTABLE by default because they are interfaces 
 *   that could be implemented by mutable types.
 */

@Immutable // Tells the compiler this object will never change
data class UserProfile(val name: String, val age: Int)

// This composable is now SKIPPABLE because UserProfile is @Immutable
@Composable
fun UserItem(user: UserProfile) {
    Text(text = "User: ${user.name}")
}

/**
 * INTERVIEW TIP: How to handle unstable Lists?
 * 1. Use Kotlinx Immutable Collections.
 * 2. Wrap the list in a class annotated with @Immutable.
 */
@Immutable
data class UserListWrapper(val items: List<UserProfile>)

// =========================================================================================
// PART 2: derivedStateOf (BUFFERING STATE)
// =========================================================================================

/**
 * Use derivedStateOf when you have a state that depends on another state that changes 
 * frequently (like scroll position), but you only care about a specific condition.
 */
@Composable
fun ScrollToTopButton() {
    val listState = rememberLazyListState()

    // BAD: This will trigger recomposition on EVERY pixel scrolled.
    // val showButton = listState.firstVisibleItemIndex > 0

    // GOOD: This only triggers recomposition when the RESULT (Boolean) changes.
    val showButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    if (showButton) {
        Button(onClick = { /* Scroll up */ }) { Text("Scroll to Top") }
    }
}

// =========================================================================================
// PART 3: DEFERRING READS (THE PHASE MODEL)
// =========================================================================================

/**
 * Compose has 3 phases: 1. Composition -> 2. Layout -> 3. Drawing.
 * If you read a state during Composition, the whole function recomposes.
 * If you read it during Layout/Draw, only those phases re-run, skipping Composition!
 */
@Composable
fun SlidingHeader(scrollOffset: Int) {
    // BAD: scrollOffset is read during Composition. 
    // The entire SlidingHeader recomposes on every scroll event.
    // Box(Modifier.offset(y = scrollOffset.dp))

    // GOOD: Using the lambda version of Modifier.offset.
    // The state read is DEFERRED to the Layout phase. 
    // Composition is skipped entirely!
    Box(
        Modifier.offset {
            IntOffset(x = 0, y = scrollOffset)
        }
    )
}

// =========================================================================================
// PART 4: LAZY LIST OPTIMIZATION (key & contentType)
// =========================================================================================

@Composable
fun EfficientList(users: List<UserProfile>) {
    LazyColumn {
        /**
         * key: Provides a stable ID for the item. Without this, if an item is inserted 
         * at the top, ALL items below it will recompose because their index changed.
         * 
         * contentType: Helps the recycler know which items can be reused for which slots.
         */
        items(
            items = users,
            key = { user -> user.name }, // Use a unique ID
            contentType = { "UserType" }
        ) { user ->
            UserItem(user)
        }
    }
}

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: What makes a Composable "Restartable" vs "Skippable"?
 * A: Restartable means it can be the starting point for a recomposition. 
 *    Skippable means it can be bypassed entirely if its inputs haven't changed. 
 *    The goal is for all UI components to be both.
 *
 * Q: How does 'remember' affect performance?
 * A: It stores a value across recompositions, avoiding expensive re-calculations. 
 *    However, remember(key) ensures that the value is re-calculated only when the key changes.
 *
 * Q: Why are anonymous lambdas a problem for performance?
 * A: Every time the parent recomposes, a new lambda object is created. If that lambda 
 *    is passed to a child, the child might think its input has changed and recompose.
 *    Fix: Use a method reference (::onItemClick) or wrap the lambda in 'remember'.
 *
 * Q: What are "Compose Compiler Metrics"?
 * A: A report generated by the compiler that labels every composable as skippable/stable. 
 *    It's the ultimate tool for finding "unstable" types that are slowing down your app.
 *
 * Q: When should I NOT use derivedStateOf?
 * A: If your state is not changing frequently (e.g. a simple Boolean toggle), 
 *    derivedStateOf adds unnecessary overhead. Only use it for high-frequency inputs 
 *    where the output changes significantly less often.
 */
