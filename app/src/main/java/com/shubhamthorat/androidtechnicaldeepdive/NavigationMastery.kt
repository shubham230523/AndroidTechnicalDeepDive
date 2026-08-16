package com.shubhamthorat.androidtechnicaldeepdive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

import kotlinx.serialization.Serializable

/**
 * ANDROID NAVIGATION MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Deep-dive into Jetpack Navigation (Type-Safe) and Navigation 3.
 */

// =========================================================================================
// PART 1: JETPACK NAVIGATION COMPOSE
// =========================================================================================

@Serializable
data object HomeRoute

@Serializable
data class ProductDetailsRoute(
    val id: Int,
    val category: String
)

@Serializable
data object SettingsGraph

@Serializable
data object ProfileRoute

@Serializable
data object PrivacyRoute

@Composable
fun Navigation2MasteryApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {

        composable<HomeRoute> {

            HomeScreenV2(
                onGoToProduct = { id, category ->
                    navController.navigate(
                        ProductDetailsRoute(
                            id = id,
                            category = category
                        )
                    )
                },
                onGoToSettings = {
                    navController.navigate(SettingsGraph)
                }
            )
        }

        composable<ProductDetailsRoute> { entry ->

            val route = entry.toRoute<ProductDetailsRoute>()

            ProductDetailsScreenV2(
                productId = route.id,
                category = route.category,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        navigation<SettingsGraph>(
            startDestination = ProfileRoute
        ) {

            composable<ProfileRoute> {

                ProfileScreen(
                    onGoToPrivacy = {
                        navController.navigate(PrivacyRoute)
                    }
                )
            }

            composable<PrivacyRoute> {

                PrivacyScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


// =========================================================================================
// PART 2: NAVIGATION 3
// =========================================================================================

@Serializable
data object DashboardKey : NavKey

@Serializable
data class DetailsKey(
    val id: String
) : NavKey

@Composable
fun Navigation3MasteryApp() {

    val backStack = rememberNavBackStack(DashboardKey)

    val entryProvider = entryProvider<NavKey> {

        entry<DashboardKey> { key ->

            DashboardScreen(
                onOpenDetails = { id ->
                    backStack.add(
                        DetailsKey(id)
                    )
                }
            )
        }

        entry<DetailsKey> { key ->

            Nav3DetailsScreen(
                id = key.id,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                }
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider
    )
}


// =========================================================================================
// UI COMPONENTS
// =========================================================================================

@Composable
fun HomeScreenV2(
    onGoToProduct: (Int, String) -> Unit,
    onGoToSettings: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Home (Type-Safe)",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                onGoToProduct(42, "Gadgets")
            }
        ) {
            Text("Go to Product 42")
        }

        Button(
            onClick = onGoToSettings
        ) {
            Text("Go to Settings")
        }
    }
}


@Composable
fun ProductDetailsScreenV2(
    productId: Int,
    category: String,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Product ID: $productId",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Category: $category"
        )

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}


@Composable
fun ProfileScreen(
    onGoToPrivacy: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = onGoToPrivacy
        ) {
            Text("Privacy")
        }
    }
}


@Composable
fun PrivacyScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Privacy",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}


@Composable
fun DashboardScreen(
    onOpenDetails: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Nav 3 Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                onOpenDetails("NAV3-001")
            }
        ) {
            Text("Open Details")
        }
    }
}


@Composable
fun Nav3DetailsScreen(
    id: String,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Nav 3 Details: $id",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}