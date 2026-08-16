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

## 🔗 External Resources
To complement the code in this repository, the following playlists by Philipp Lackner are highly recommended:

- [Kotlin Coroutines Playlist](https://youtube.com/playlist?list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&si=Au8KAGchorYvQI2x)
- [Kotlin Flows Playlist](https://youtube.com/playlist?list=PLQkwcJG4YTCQHCppNAQmLsj_jW38rU9sC&si=4NHbJu7CJ8fk11zJ)

## 🛠️ How to use this project
1. Open the `.kt` files in the `com.shubhamthorat.androidtechnicaldeepdive` package.
2. Read the comments explaining the "Why" and "How".
3. Copy the code into your own scratchpad or run them in `MainActivity.kt` to observe behaviors in Logcat.
4. Practice writing the syntax from scratch to build muscle memory for interviews.
