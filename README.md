# Android Technical Deep Dive 🚀

This project is a comprehensive guide to mastering core Android development concepts, specifically tailored for technical interviews. It contains deep-dive code examples, syntax guides, and common interview scenarios.

## 📚 Topics Covered

### 1. Kotlin Coroutines (0 to 100)
- **Basics:** `launch`, `async`, `runBlocking`, and `suspend` functions.
- **Dispatchers:** Deep dive into `Main`, `IO`, `Default`, and the behavior of `Unconfined`.
- **Exception Handling:** The difference between `launch` and `async` error propagation.
- **Structured Concurrency:** Understanding `Job` vs. `SupervisorJob` and the "Sibling Cancellation Trap".
- **Context Switching:** Best practices using `withContext` for main-safety.

### 2. Kotlin Flows (0 to 100)
- **Stream Basics:** Understanding Cold Streams and Flow builders (`flow { }`, `flowOf`, `asFlow`).
- **Operators:** Transformation (`map`, `filter`, `transform`) and Terminal operators (`collect`, `toList`, `first`).
- **Context & Buffering:** Using `flowOn` and handling backpressure with `buffer()` and `conflate()`.
- **Hot Flows:** Deep dive into `StateFlow` (UI State) vs `SharedFlow` (Events/Actions).
- **Combining & Flattening:** `zip`, `combine`, `flatMapConcat`, `flatMapMerge`, and `flatMapLatest`.
- **Channels:** Understanding Hot streams, `Channel` types (Rendezvous, Buffered, Conflated, Unlimited), and the difference between Flow and Channel.

### 3. Android Navigation (0 to 100)
- **Type-Safe Navigation:** Using `@Serializable` objects and classes instead of String routes.
- **NavHost & NavController:** Setting up the navigation container and controller.
- **Arguments:** Passing data between screens using type-safe data classes.
- **Nested Navigation:** Grouping routes using `navigation` graphs.
- **Bottom Navigation:** Best practices for tab switching (saveState/restoreState).
- **Deep Linking:** Handling external app links type-safely.
- **Interview Strategy:** Handling complex arguments and multiple backstacks.

### 4. Android Hilt Mastery (0 to 100)
- **Setup:** `@HiltAndroidApp` and `@AndroidEntryPoint`.
- **Injection:** Constructor vs. Field injection.
- **Modules:** `@Module`, `@Binds` vs `@Provides`, and Scopes.
- **Advanced:** Assisted Injection, Qualifiers, and manual Entry Points.

### 5. Room Database Mastery (0 to 100)
- **Schema:** `@Entity`, `@PrimaryKey`, `@Embedded`, and Type Converters.
- **DAO:** CRUD operations with Coroutines and reactive updates using Flow.
- **Advanced:** Relationships (1:1, 1:N) and manual Database Migrations.
- **Singleton Pattern:** Thread-safe database instance management.

### 6. WorkManager Mastery (0 to 100)
- **Workers:** Implementing `CoroutineWorker` and handling `Result`.
- **WorkRequests:** One-time vs Periodic tasks and Initial Delays.
- **Constraints:** Network, Battery, and Charging requirements.
- **Task Management:** Work Chaining, Unique Work, and observing `WorkInfo`.

### 7. MVVM Architecture Mastery (0 to 100)
- **Layers:** Clear separation of Model, Repository, ViewModel, and View.
- **UI State:** Using Sealed Classes to represent Loading/Success/Error.
- **Flows:** `StateFlow` for persistent state and `SharedFlow` for one-time events.
- **Clean Integration:** Introduction to Use Cases (Domain Layer).

### 8. MVI Architecture Mastery (0 to 100)
- **Principle:** Unidirectional Data Flow (UDF).
- **ViewState:** A single, immutable object for the entire UI state.
- **Intent & Effect:** Handling user actions and one-time side effects via Channels.
- **State Machine:** ViewModel as a Reducer to update state.

### 9. Clean Architecture Mastery (0 to 100)
- **Dependency Rule:** Logic must point inwards toward the pure Kotlin Domain layer.
- **Domain:** Entities, Repository Interfaces, and Use Cases.
- **Data:** DTOs, Mappers, and Repository implementations.
- **Decoupling:** Ensuring frameworks and external APIs don't leak into core logic.

### 10. Retrofit Mastery (0 to 100)
- **API Definition:** `@GET`, `@POST`, `@Path`, `@Query`, and `@Body`.
- **OkHttp Engine:** Timeouts and Interceptors (Logging & Auth Tokens).
- **Concurrency:** Non-blocking `suspend` functions and error handling with `Response<T>`.

### 11. Offline-First App Mastery (0 to 100)
- **Single Source of Truth:** Why the UI always observes the local DB.
- **NetworkBoundResource:** Strategy for fetching remote data and updating local cache.
- **Background Syncing:** Using WorkManager to sync offline actions when connection returns.

### 12. Testing App Mastery (0 to 100)
- **Unit Testing:** Fast tests for business logic using JUnit 4 and MockK.
- **Integration Testing:** In-Memory Room database testing.
- **UI Testing:** Basics of Espresso and Compose Test Rules.
- **Pyramid:** Understanding the 70-20-10 distribution of tests.

### 13. Gradle & Build System Mastery (0 to 100)
- **Lifecycle:** Initialization, Configuration, and Execution phases.
- **Configurations:** `implementation` vs `api` vs `kapt`.
- **Build Variants:** Build Types (R8/ProGuard) and Product Flavors.
- **Modern Management:** Version Catalogs (`libs.versions.toml`) and KTS syntax.

### 14. Android Services Mastery (0 to 100)
- **Service Types:** Started, Bound, and Foreground services.
- **Lifecycles:** `onStartCommand` flags and proper service management.
- **Background Processing:** Service vs WorkManager and threading myths.

### 15. Android Paging 3 Mastery (0 to 100)
- **Core Components:** `PagingSource`, `Pager`, and `PagingData`.
- **ViewModel Integration:** Config, `cachedIn(viewModelScope)`, and transformations.
- **Compose UI:** `collectAsLazyPagingItems`, `items` count, and handling `LoadState`.
- **Advanced:** `RemoteMediator` for offline-first caching with Room.
- **Interview Tactics:** Understanding the difference between `PagingSource` and `RemoteMediator`.

### 16. Android DataStore & SharedPreferences Mastery (0 to 100)
- **Legacy:** SharedPreferences syntax, `apply()` vs `commit()`, and pitfalls.
- **Preferences DataStore:** Key-value storage using Flow and Coroutines.
- **Proto DataStore:** Typed object storage using Serializers.
- **Migration:** Safe strategies to move from legacy to modern storage.

### 17. Android Keystore Mastery (0 to 100)
- **Hardware Security:** Understanding TEE (Trusted Execution Environment) and SE (Secure Element).
- **Key Generation:** Using `KeyGenerator` (AES) and `KeyPairGenerator` (RSA).
- **Encryption/Decryption:** Implementing a robust `CryptoManager` for secure data handling.
- **Security Features:** User authentication requirements and biometric binding.

### 18. Android Broadcast Receiver Mastery (0 to 100)
- **Publish-Subscribe:** System vs. Custom broadcasts.
- **Static vs. Dynamic:** Manifest-declared vs. Context-registered receivers and restrictions.
- **Lifecycle:** Safe registration/unregistration to prevent memory leaks.
- **Modern Android:** Handling Android 14+ export flags and Oreo background limits.

### 19. Android Content Provider Mastery (0 to 100)
- **Concept:** Data sharing abstraction and URI structure.
- **Implementation:** Overriding the 6 core methods (`query`, `insert`, etc.).
- **UriMatcher:** Routing requests to specific data types or records.
- **MIME Types:** Defining directory vs. item types for Intent resolution.
- **Interview Tactics:** Thread-safety in Binder threads and security permissions.

### 20. Android Intents & Deep Linking Mastery (0 to 100)
- **Intents:** Explicit vs. Implicit intents and `resolveActivity()` safety.
- **PendingIntents:** Usage in notifications and Android 12+ mutability flags.
- **Deep Linking:** Handling custom URI schemes and intent filters.
- **App Links:** Domain verification using Digital Asset Links (JSON).
- **Security:** Preventing Intent Spoofing and handling `TransactionTooLargeException`.

### 21. Android Jetpack Compose Mastery (0 to 100)
- **Mental Model:** Declarative UI vs. Imperative Views and the Phase Model.
- **State:** `remember`, `rememberSaveable`, `mutableStateOf`, and State Hoisting.
- **Side Effects:** `LaunchedEffect`, `DisposableEffect`, and `SideEffect` usage.
- **Optimization:** Smart Recomposition, stability (`@Stable`), and `derivedStateOf`.
- **Layouts:** Modifier chaining and order, and efficient lists with `LazyColumn`.

### 22. Advanced Kotlin & Internals Mastery (0 to 100)
- **Internals:** `inline`, `noinline`, `crossinline`, and Bytecode analysis.
- **Generics:** `reified` type parameters and overcoming type erasure.
- **Delegation:** Property delegates (`by lazy`, `observable`) and Class delegation.
- **Scope Functions:** Definitive guide to `let`, `run`, `apply`, `also`, and `with`.
- **Memory Optimization:** Value classes (`@JvmInline`) and memory-efficient lambdas.

### 23. Kotlin Multiplatform (KMP) Mastery (0 to 100)
- **Architecture:** Shared Logic vs. Native UI philosophy and Compose Multiplatform.
- **Interoperability:** The `expect`/`actual` mechanism for platform-specific APIs.
- **Stack:** Ktor for networking, SQLDelight for database, and Koin for DI.
- **Concurrency:** Handling Coroutines and Flows across Android and iOS (Swift interop).
- **Interviews:** Comparing KMP with Flutter/React Native and understanding the new memory manager.

### 24. Compose Multiplatform Mastery (0 to 100)
- **Shared UI:** Building pixel-perfect interfaces in `commonMain` using Skia/Skiko.
- **Interoperability:** Wrapping native Android Views and iOS UIKit views in shared Composables.
- **Resources:** Sharing images, strings, and fonts across platforms with the Resources library.
- **Navigation:** Multiplatform stacks using libraries like Voyager or Decompose.
- **Interview Tactics:** Understanding the rendering pipeline, performance on iOS, and KMP vs. CMP.

### 25. Android Agent Development Kit (AADK) Mastery (0 to 100)
- **Mental Model:** AI Agents vs. Chatbots and the Plan-Execute-Observe loop.
- **Tools:** Defining agent capabilities using `@Tool` and metadata-driven reasoning.
- **Hybrid Orchestration:** Balancing Cloud reasoning with On-device privacy (Gemini Nano).
- **Security:** Handling prompt injection, tool permissions, and human-in-the-loop validation.
- **Interviews:** Distinguishing AADK from AGDK and architecting privacy-first agents.

### 26. Compose Performance & Recomposition Mastery (0 to 100)
- **Stability:** Mastering `@Stable`, `@Immutable`, and handling unstable collections.
- **derivedStateOf:** Buffering high-frequency state updates (Scroll, Timers).
- **Phase Deferral:** Deferring state reads to Layout and Drawing phases to skip Composition.
- **Lazy List Hooks:** Using `key` and `contentType` for optimal item recycling.
- **Interviews:** Understanding restartable vs. skippable composables and compiler metrics.

## 🔗 External Resources
To complement the code in this repository, the following playlists by Philipp Lackner are highly recommended:

- [Kotlin Coroutines Playlist](https://youtube.com/playlist?list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&si=Au8KAGchorYvQI2x)
- [Kotlin Flows Playlist](https://youtube.com/playlist?list=PLQkwcJG4YTCQHCppNAQmLsj_jW38rU9sC&si=4NHbJu7CJ8fk11zJ)

### Official Navigation Resources
- [Type-Safe Navigation in Compose](https://developer.android.com/develop/ui/compose/navigation#type-safe)
- [Nested Navigation Graphs](https://developer.android.com/develop/ui/compose/navigation#nested-graphs)

## 🛠️ How to use this project
1. Open the `.kt` files in the `com.shubhamthorat.androidtechnicaldeepdive` package.
2. Read the comments explaining the "Why" and "How".
3. Copy the code into your own scratchpad or run them in `MainActivity.kt` to observe behaviors in Logcat.
4. Practice writing the syntax from scratch to build muscle memory for interviews.
