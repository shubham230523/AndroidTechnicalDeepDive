package com.shubhamthorat.androidtechnicaldeepdive

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

/**
 * MASTERING ANDROID NAVIGATION (COMPOSE): 0 TO 100
 *
 * CONCEPT 1: Type-Safe Navigation (The Modern Way)
 * Gone are the days of string routes like "home_screen".
 * Now we use @Serializable objects and classes.
 */

// 1. Define your destinations
@Serializable
object HomeRoute

@Serializable
data class ProfileRoute(
    val id: String,
    val name: String
)

@Serializable
object SettingsGraph

@Serializable
object SettingsRoute

/**
 * CONCEPT 2: NavHost and NavController
 * NavController: The brain. It manages navigation and the backstack.
 * NavHost: The container that displays the destinations.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        // Simple Destination
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToProfile = { id, name ->
                    navController.navigate(ProfileRoute(id, name))
                }
            )
        }

        // Destination with Arguments
        composable<ProfileRoute> { backStackEntry ->
            // Extract arguments using toRoute<T>()
            val profile: ProfileRoute = backStackEntry.toRoute()
            ProfileScreen(profile.id, profile.name)
        }

        /**
         * CONCEPT 3: Nested Navigation (NavGraphs)
         * Used to group related screens (e.g., Auth flow, Onboarding).
         * Helps in modularizing the app and clear backstack management.
         */
        navigation<SettingsGraph>(startDestination = SettingsRoute) {
            composable<SettingsRoute> {
                SettingsScreen()
            }
        }
    }
}

/**
 * CONCEPT 4: Navigation Actions & Backstack
 * - navigate(route): Push a new destination.
 * - popBackStack(): Go back one step.
 * - popUpTo(route) { inclusive = true }: Clear backstack until a point.
 */
@Composable
fun HomeScreen(onNavigateToProfile: (String, String) -> Unit) {
    Column {
        Text("Home Screen")
        Button(onClick = { onNavigateToProfile("123", "Shubham") }) {
            Text("Go to Profile")
        }
    }
}

@Composable
fun ProfileScreen(id: String, name: String) {
    Column {
        Text("Profile Screen")
        Text("ID: $id, Name: $name")
    }
}

@Composable
fun SettingsScreen() {
    Text("Settings Screen")
}

/**
 * CONCEPT 5: Bottom Navigation & Multiple Backstacks
 * Interview Tip: When switching tabs, you should save and restore state
 * so the user doesn't lose their place in each tab.
 */
fun bottomNavExample(navController: NavController) {
    navController.navigate(HomeRoute) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

/**
 * CONCEPT 6: Deep Linking
 * Allows users to open specific screens in your app via a URL.
 */
@Serializable
data class DetailRoute(val id: Int)

val deepLinks = listOf(
    navDeepLink<DetailRoute>(basePath = "https://www.example.com/detail")
)

// In NavHost:
// composable<DetailRoute>(deepLinks = deepLinks) { ... }

/**
 * CONCEPT 7: Custom Types (Advanced Arguments)
 * Interview Question: Can we pass complex objects in Navigation?
 * Answer: Yes, by using a custom NavType, but it's generally discouraged.
 * Best practice is to pass the ID and fetch the data.
 */
@Serializable
data class User(val id: Int, val name: String)

// To pass this, you'd define:
// val UserType = object : NavType<User>(isNullableAllowed = false) { ... }
// Then in composable<DetailRoute>(typeMap = mapOf(typeOf<User>() to UserType))

/**
 * NAVIGATION INTERVIEW CHEAT SHEET:
 * 1. Type-Safety: Use @Serializable (Route as data class/object).
 * 2. NavHost: The container mapping routes to Composables.
 * 3. NavController: Handles the actual navigation logic.
 * 4. toRoute<T>(): The extension to extract type-safe arguments.
 * 5. Deep Linking: Routes can be mapped to external URLs.
 * 6. Multiple Backstacks: Essential for Bottom Nav (saveState/restoreState).
 */
