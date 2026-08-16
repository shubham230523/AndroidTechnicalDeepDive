package com.shubhamthorat.androidtechnicaldeepdive

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * MASTERING KOTLIN COROUTINES: 0 TO 100
 *
 * Reference Playlist by Philipp Lackner:
 * Coroutines: https://youtube.com/playlist?list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_&si=Au8KAGchorYvQI2x
 *
 * CONCEPT 1: What is a Coroutine?
 * Coroutines = "Cooperative routines". They are lightweight threads that allow 
 * asynchronous code to be written in a synchronous style.
 *
 * 'suspend' keyword: The most important concept. It tells the compiler that this
 * function can pause the coroutine and resume it later without blocking the thread.
 */

object CoroutineBasics {

    // A suspend function can only be called from a coroutine or another suspend function.
    suspend fun doNetworkCall(): String {
        delay(1000) // Non-blocking delay (Thread is free to do other things)
        return "Data from Server"
    }

    /**
     * CONCEPT 2: Coroutine Builders
     * 1. launch: "Fire and forget". Returns a Job. Best for tasks where you don't need a result.
     * 2. async: "Fire and wait". Returns Deferred<T>. Best for tasks where you need a result.
     * 3. runBlocking: Blocks the current thread until finished. Use ONLY for tests/main functions.
     */
    fun buildersExample() = runBlocking {
        println("Main Thread: ${Thread.currentThread().name}")

        // Launch - Fire and Forget
        val job: Job = launch {
            val data = doNetworkCall()
            println("Launch result: $data")
        }

        // Async - Get a result
        val deferred: Deferred<String> = async {
            doNetworkCall()
        }
        
        // Wait for async result
        println("Async result: ${deferred.await()}")
        
        job.join() // Wait for launch to finish
    }
}

/**
 * CONCEPT 3: Coroutine Dispatchers
 * They determine which thread(s) the coroutine uses.
 */
object DispatcherMastery {
    fun dispatchersDemo() = runBlocking {
        // Main: UI updates (Main thread)
        launch(Dispatchers.Main) { println("Main: ${Thread.currentThread().name}") }

        // IO: Optimized for Network/Disk (Background thread pool)
        launch(Dispatchers.IO) { println("IO: ${Thread.currentThread().name}") }

        // Default: Optimized for CPU intensive work like sorting (Background thread pool)
        launch(Dispatchers.Default) { println("Default: ${Thread.currentThread().name}") }
        
        // Unconfined: Runs on current thread until first suspension (Tricky!)
        launch(Dispatchers.Unconfined) { println("Unconfined: ${Thread.currentThread().name}") }
    }
}

/**
 * CONCEPT 4: withContext (CRITICAL INTERVIEW TOPIC)
 * withContext is a suspend function used to switch dispatchers safely.
 * It is "Main-safe", meaning you can call it from the Main thread without blocking.
 */
class UserRepository {
    suspend fun getUserData(): String = withContext(Dispatchers.IO) {
        // Switches to IO thread for network call
        delay(1000)
        "User Profile Data"
    } // Automatically switches back to the original thread (e.g., Main) after completion
}

/**
 * CONCEPT 5: Scopes & Jobs
 * Coroutines must run in a Scope to manage their lifecycle.
 * - viewModelScope: Cancelled when ViewModel is cleared (Best for Android).
 * - lifecycleScope: Cancelled when Activity/Fragment is destroyed.
 * - GlobalScope: Lives as long as the app. AVOID this in production (causes leaks).
 */
object ScopeAndCancellation {
    fun cancellationExample() = runBlocking {
        val job = launch {
            repeat(100) { i ->
                println("Working $i...")
                delay(500)
            }
        }
        delay(1600)
        println("Tired of waiting, cancelling...")
        job.cancelAndJoin() // Stops the coroutine safely
        println("Cancelled!")
    }
}

/**
 * CONCEPT 6: Exception Handling
 * 1. try-catch: Standard way inside a coroutine.
 * 2. CoroutineExceptionHandler: Catch errors globally for a scope.
 * 3. SupervisorJob: Failure of one child does NOT kill other children (V. Important).
 */
object ExceptionMastery {
    
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught: ${exception.message}")
    }

    fun supervisorDemo() = runBlocking {
        val supervisor = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.Default + supervisor + handler)

        val child1 = scope.launch {
            println("Child 1 failing...")
            throw RuntimeException("Child 1 Error")
        }

        val child2 = scope.launch {
            delay(100)
            println("Child 2 still running because of SupervisorJob!")
        }

        joinAll(child1, child2)
    }
}

/**
 * DEEP DIVE: Dispatchers.Unconfined
 * "Unconfined" is different because it doesn't confine the coroutine to any specific thread.
 * 1. It starts the coroutine in the caller's thread.
 * 2. After a suspension point, it resumes in whatever thread the suspending function was running on.
 */
object UnconfinedDeepDive {
    fun demo() = runBlocking {
        launch(Dispatchers.Unconfined) {
            println("1. Starts in thread: ${Thread.currentThread().name}") // Likely 'main'
            
            // This suspension happens on the Default dispatcher
            withContext(Dispatchers.Default) {
                delay(100)
                println("2. Working in thread: ${Thread.currentThread().name}")
            }
            
            // After withContext, it doesn't switch back! It stays on the last thread.
            println("3. Resumes in thread: ${Thread.currentThread().name}") 
        }
    }
    // INTERVIEW TIP: Avoid Unconfined in general UI code. It is mainly used for 
    // specific tests where you want immediate execution without thread switching overhead.
}

/**
 * DEEP DIVE: Advanced Exception Handling
 * CRITICAL INTERVIEW QUESTION: How do 'launch' and 'async' handle exceptions differently?
 */
object ExceptionDeepDive {

    /**
     * 1. launch: Exceptions are propagated immediately to the parent.
     * If not caught by a CoroutineExceptionHandler, the app crashes.
     */
    fun launchException() = runBlocking {
        val scope = CoroutineScope(Job())
        scope.launch {
            println("Launch: Throwing exception...")
            throw ArithmeticException("Launch Error")
        }
    }

    /**
     * 2. async: Exceptions are "captured" in the Deferred result.
     * The exception is only thrown when you call .await().
     * HOWEVER, if async is a child of a normal Job, it still fails the parent!
     */
    fun asyncException() = runBlocking {
        val scope = CoroutineScope(Job())
        val deferred = scope.async {
            println("Async: Throwing exception...")
            throw ArithmeticException("Async Error")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println("Caught in await: ${e.message}")
        }
    }

    /**
     * 3. The "Root" Coroutine Rule:
     * Only the root coroutine (the one created directly by CoroutineScope) 
     * can use CoroutineExceptionHandler. Adding it to a child launch won't work.
     */
    fun rootHandlerDemo() {
        val handler = CoroutineExceptionHandler { _, e -> println("Handled: $e") }
        val scope = CoroutineScope(Job() + handler) // Handler at the ROOT

        scope.launch {
            launch { // Child launch
                throw RuntimeException("Deep Error")
            }
        }
    }
}

/**
 * DEEP DIVE: The "Handler vs Supervisor" Trap
 * Even if you have a handler, a normal Job will cancel ALL siblings.
 */
object SiblingCancellationDemo {

    val handler = CoroutineExceptionHandler { _, e -> println("Handler caught: $e") }

    // SCENARIO 1: Normal Job + Handler
    // Result: Child 1 fails -> Parent cancels Child 2 -> Handler catches error.
    fun normalJobBehavior() = runBlocking {
        val scope = CoroutineScope(Job() + handler)
        
        scope.launch {
            delay(100)
            throw RuntimeException("Child 1 failed")
        }

        scope.launch {
            delay(500)
            println("Child 2: I will NEVER be printed because my sibling failed")
        }
    }

    // SCENARIO 2: SupervisorJob + Handler
    // Result: Child 1 fails -> Parent IGNORES it -> Child 2 finishes -> Handler catches error.
    fun supervisorJobBehavior() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + handler)

        scope.launch {
            delay(100)
            throw RuntimeException("Child 1 failed")
        }

        scope.launch {
            delay(500)
            println("Child 2: I WILL be printed because of SupervisorJob!")
        }
    }
}

/**
 * INTERVIEW SUMMARY:
 * - What is a Coroutine? A framework for managing background threads with less boilerplate.
 * - Suspend vs Block? Suspend releases the thread; Block holds the thread captive.
 * - launch vs async? launch = Job; async = Deferred (await()).
 * - withContext? Safely changes the thread context for a specific block of code.
 * - SupervisorJob? Prevents failure propagation to siblings.
 */
