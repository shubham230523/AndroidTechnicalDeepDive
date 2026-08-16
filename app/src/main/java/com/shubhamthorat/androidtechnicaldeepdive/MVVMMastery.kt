package com.shubhamthorat.androidtechnicaldeepdive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ANDROID MVVM ARCHITECTURE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * MVVM (Model-View-ViewModel) is the recommended architecture by Google.
 * It promotes Separation of Concerns, Testability, and Lifecycle Awareness.
 */

// =========================================================================================
// PART 1: THE MODEL (Data & Business Logic)
// =========================================================================================

/**
 * Entity: Represents the data structure.
 */
data class Product(val id: Int, val name: String, val price: Double)

/**
 * Data Source: Represents where the data comes from (API or DB).
 */
interface ProductDataSource {
    suspend fun getProducts(): List<Product>
}

class RemoteProductDataSource @Inject constructor() : ProductDataSource {
    override suspend fun getProducts(): List<Product> {
        delay(1000) // Simulating network delay
        return listOf(
            Product(1, "Smartphone", 699.99),
            Product(2, "Laptop", 1299.99)
        )
    }
}

// =========================================================================================
// PART 2: THE REPOSITORY (Single Source of Truth)
// =========================================================================================

/**
 * Repository: Mediates between different data sources.
 * It abstracts the data source from the ViewModel.
 */
class ProductRepository @Inject constructor(
    private val remoteDataSource: ProductDataSource
) {
    suspend fun fetchProducts(): List<Product> {
        // Logic to decide between Local DB and Remote API goes here
        return remoteDataSource.getProducts()
    }
}

// =========================================================================================
// PART 3: THE VIEWMODEL (State & Logic)
// =========================================================================================

/**
 * UI State: Represents what is currently shown on the screen.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * ViewModel: Holds and manages UI-related data in a lifecycle-conscious way.
 * It survives configuration changes (like screen rotation).
 */
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    // 1. StateFlow: Holds state. Always has a value. Emits the last known state to new collectors.
    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    // 2. SharedFlow: Emits events (one-time). Does not hold state. Good for SnackBar/Navigation.
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val products = repository.fetchProducts()
                _uiState.value = UiState.Success(products)
                _eventFlow.emit("Products loaded successfully!")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}

// =========================================================================================
// PART 4: THE VIEW (UI Layer)
// =========================================================================================

/**
 * The View (Activity/Fragment/Compose): Only responsible for displaying data and forwarding
 * user actions to the ViewModel. It should NOT contain business logic.
 *
 * Interview Note: In Compose, we use 'collectAsStateWithLifecycle()' for safety.
 */

/*
@Composable
fun ProductScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> {
            ProductList((state as UiState.Success).data)
        }
        is UiState.Error -> Text("Error: ${(state as UiState.Error).message}")
    }
}
*/

// =========================================================================================
// PART 5: CLEAN ARCHITECTURE (The Domain Layer)
// =========================================================================================

/**
 * For true "100" mastery, mention Use Cases (Interactors).
 * UseCase: Contains exactly one piece of business logic.
 * ViewModel calls UseCase -> UseCase calls Repository.
 */
class GetProductsUseCase @Inject constructor(private val repository: ProductRepository) {
    suspend operator fun invoke() = repository.fetchProducts()
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. Why MVVM? Decouples UI from Logic, prevents memory leaks, makes testing easier.
// 2. StateFlow vs SharedFlow: StateFlow is for "Current State", SharedFlow is for "Events".
// 3. ViewModelScope: Automatically cancels coroutines when the ViewModel is cleared.
// 4. Testability: You can unit test the ViewModel by mocking the Repository.
// =========================================================================================
