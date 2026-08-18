package com.shubhamthorat.androidtechnicaldeepdive

/**
 * KOTLIN MULTIPLATFORM (KMP) MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Kotlin Multiplatform (KMP) allows you to share business logic across different platforms 
 * (Android, iOS, Web, Desktop) while keeping the UI native.
 *
 * KEY CONCEPTS:
 * 1. commonMain: Contains pure Kotlin code shared across ALL platforms.
 * 2. androidMain / iosMain: Contains platform-specific implementations.
 * 3. expect / actual: The mechanism to call platform-specific APIs from shared code.
 */

// =========================================================================================
// PART 1: THE EXPECT / ACTUAL MECHANISM
// =========================================================================================

/**
 * 1. 'expect' declaration in commonMain:
 * This tells the compiler: "I expect this class/function to be implemented by every platform."
 */
expect class PlatformLogger() {
    fun log(message: String)
}

/**
 * 2. 'actual' implementation in androidMain:
 * Uses Android-specific APIs (e.g. android.util.Log).
 */
/* 
// Note: This would live in src/androidMain/kotlin/
actual class PlatformLogger actual constructor() {
    actual fun log(message: String) {
        android.util.Log.d("KMP_LOG", message)
    }
}
*/

/**
 * 3. 'actual' implementation in iosMain:
 * Uses iOS-specific APIs (Objective-C/Swift interop).
 */
/*
// Note: This would live in src/iosMain/kotlin/
actual class PlatformLogger actual constructor() {
    actual fun log(message: String) {
        platform.Foundation.NSLog("KMP_LOG: %@", message)
    }
}
*/

// =========================================================================================
// PART 2: SHARED ARCHITECTURE (THE CLEAN WAY)
// =========================================================================================

/**
 * In KMP, we typically share:
 * - Data Models (POJOs)
 * - Repository Layer (Network + Database)
 * - Domain Layer (Use Cases / Business Logic)
 * - ViewModels (sometimes, using libraries like KMP-NativeCoroutines)
 */

data class Greeting(val text: String)

class GreetingGenerator {
    fun greet(): Greeting {
        return Greeting("Hello from Shared Logic!")
    }
}

// =========================================================================================
// PART 3: MULTIPLATFORM LIBRARIES (THE KMP STACK)
// =========================================================================================

/**
 * To share network and database logic, you use KMP-compatible libraries:
 * 1. Ktor: Asynchronous HTTP client for multiplatform.
 * 2. SQLDelight: Generates typesafe Kotlin APIs from SQL statements (Multiplatform DB).
 * 3. Kotlinx Serialization: JSON parsing for KMP.
 * 4. Koin: Dependency Injection for multiplatform.
 */

// Example of a Ktor setup in commonMain (Conceptual)
/*
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}
*/

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: KMP vs Flutter / React Native?
 * A: KMP shares BUSINESS LOGIC while keeping UI NATIVE. Flutter/RN share both UI and logic. 
 *    KMP provides better performance and full access to platform-specific APIs without 
 *    the overhead of a "bridge" or custom rendering engine.
 *
 * Q: What is Compose Multiplatform?
 * A: It's a library that extends Jetpack Compose to share UI code as well. While KMP 
 *    is about shared logic, Compose Multiplatform is about shared UI.
 *
 * Q: How are Coroutines handled on iOS?
 * A: KMP uses the native memory manager. To consume Flow/Suspend functions in Swift, 
 *    you often need a wrapper or a library like 'KMP-NativeCoroutines' to convert 
 *    them into Swift Concurrency (Async/Await) or Combine.
 *
 * Q: When NOT to use KMP?
 * A: If the app is extremely UI-heavy with very little business logic, or if you don't 
 *    have Kotlin expertise in your team.
 *
 * Q: What is the benefit of SQLDelight over Room in KMP?
 * A: SQLDelight was built for multiplatform from day one. It generates code that works 
 *    on SQLite on Android and SQLite (via FMDB/Native) on iOS. Room just added KMP 
 *    support recently, but SQLDelight is still the industry standard for shared DB.
 */
