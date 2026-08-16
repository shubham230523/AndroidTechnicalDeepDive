package com.shubhamthorat.androidtechnicaldeepdive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ANDROID CLEAN ARCHITECTURE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Clean Architecture (by Robert C. Martin / Uncle Bob) separates code into layers.
 * The core rule: Dependencies point INWARDS. Domain layer knows NOTHING about Data or UI.
 */

// =========================================================================================
// PART 1: THE DOMAIN LAYER (Business Logic - Inner Most Layer)
// This layer is pure Kotlin. It has NO dependencies on Android or external libraries.
// =========================================================================================

/**
 * 1. Entity: Plain Kotlin objects representing core business data.
 */
data class UserEntity(val id: Int, val name: String, val email: String)

/**
 * 2. Repository Interface: Defines the contract. The Data layer will implement this.
 * This allows the Domain layer to remain independent of data sources.
 */
interface UserRepository {
    suspend fun getUser(id: Int): UserEntity
}

/**
 * 3. Use Case (Interactor): Contains exactly one piece of business logic.
 * It coordinates the flow of data to and from the entities.
 */
class GetUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int): UserEntity {
        // Business logic: Maybe filter data or combine multiple repositories here
        return repository.getUser(id)
    }
}

// =========================================================================================
// PART 2: THE DATA LAYER (Implementation Details)
// This layer handles API calls, DB operations, and DTO mapping.
// =========================================================================================

/**
 * 4. Data Model (DTO): Represents data from a network or database.
 * We separate DTOs from Entities to prevent API changes from breaking the Domain layer.
 */
data class UserDto(val userId: Int, val fullName: String, val emailAddr: String)

/**
 * 5. Mapper: Converts DTOs to Domain Entities.
 */
fun UserDto.toDomain(): UserEntity {
    return UserEntity(id = userId, name = fullName, email = emailAddr)
}

/**
 * 6. Repository Implementation: The concrete implementation of the Domain interface.
 */
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun getUser(id: Int): UserEntity {
        val dto = remoteDataSource.fetchUser(id)
        return dto.toDomain() // Mapping happens here
    }
}

class UserRemoteDataSource @Inject constructor() {
    suspend fun fetchUser(id: Int): UserDto = UserDto(id, "John Doe", "john@example.com")
}

// =========================================================================================
// PART 3: THE PRESENTATION LAYER (UI Logic)
// This layer is responsible for showing data and handling user input.
// =========================================================================================

/**
 * 7. ViewModel: Communicates ONLY with Use Cases.
 */
class UserPresenterViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<UserEntity?>(null)
    val userState: StateFlow<UserEntity?> = _userState

    fun loadUser(id: Int) {
        viewModelScope.launch {
            val user = getUserUseCase(id)
            _userState.value = user
        }
    }
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. Dependency Rule: Dependencies point Inwards. Domain -> Data (via interface) and Domain -> Presentation.
// 2. Why Use Cases? They make business logic reusable and easy to test in isolation.
// 3. Why Mappers? To decouple the internal business logic from external data schema changes.
// 4. Testability: You can test the Domain layer with 100% JUnit (no Android needed).
// 5. Scalability: Multiple teams can work on different layers without stepping on each other.
// =========================================================================================
