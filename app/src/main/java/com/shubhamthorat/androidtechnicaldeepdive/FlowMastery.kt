package com.shubhamthorat.androidtechnicaldeepdive

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.*

/**
 * MASTERING KOTLIN FLOWS: 0 TO 100
 *
 * Reference Playlist by Philipp Lackner:
 * Flows: https://youtube.com/playlist?list=PLQkwcJG4YTCQHCppNAQmLsj_jW38rU9sC&si=4NHbJu7CJ8fk11zJ
 *
 * CONCEPT 1: What is Flow?
 * A Flow is a "Cold Stream". It doesn't emit values until someone starts collecting it.
 * Think of it like a YouTube video: it doesn't play until you click it.
 */

object FlowBasics {

    // 1. Simple Flow Builder
    fun simpleFlow(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100) // Simulate network/long task
            emit(i) // Sending value to collector
        }
    }

    // 2. Different Builders
    val flowOfBuilder = flowOf(1, 2, 3)
    val asFlowBuilder = (1..5).asFlow()
}

/**
 * CONCEPT 2: Operators (Intermediate)
 * Operators transform the data stream.
 */
object FlowOperators {

    fun transformationOperators() = runBlocking {
        val flow = (1..3).asFlow()

        // Map: Transform each value
        flow.map { it * 2 }.collect { println("Mapped: $it") }

        // Filter: Only allow specific values
        flow.filter { it % 2 == 0 }.collect { println("Filtered: $it") }

        // Transform: More flexible than map, can emit multiple values
        flow.transform { value ->
            emit("Value $value")
            emit("Transformed $value")
        }.collect { println(it) }
    }
}

/**
 * CONCEPT 3: Terminal Operators
 * These are the ones that actually "start" the flow.
 */
object TerminalOperators {
    suspend fun demo() {
        val flow = (1..5).asFlow()

        val list = flow.toList() // Converts flow to list
        val first = flow.first() // Gets first element
        val sum = flow.reduce { a, b -> a + b } // Accumulates values
    }
}

/**
 * CONCEPT 4: Flow Context & flowOn
 * CRITICAL INTERVIEW QUESTION: How to change the dispatcher in Flow?
 * Answer: Use flowOn(). It changes the context of EVERYTHING ABOVE it.
 */
fun flowOnDemo() = flow {
    println("Emitting on ${Thread.currentThread().name}")
    emit(1)
}.flowOn(Dispatchers.IO) // This emission happens on IO thread

/**
 * CONCEPT 5: Exception Handling
 * Use .catch() to handle errors in the stream.
 */
fun exceptionHandling() = flow {
    emit(1)
    throw RuntimeException("Error!")
}.catch { e ->
    emit(-1) // Emit a fallback value
    println("Caught: ${e.message}")
}

/**
 * CONCEPT 6: HOT FLOWS (StateFlow vs SharedFlow)
 * Unlike Flow, these are "Hot" (like a Radio Station). 
 * They emit even if no one is listening.
 */
class HotFlowsDemo {
    
    // StateFlow: Always has a state. Used for UI State. 
    // Similar to LiveData but requires an initial value.
    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    // SharedFlow: Used for Events (Snackbars, Navigation).
    // Doesn't hold state, just broadcasts events.
    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    suspend fun update() {
        _stateFlow.value = 5 // Updates state
        _sharedFlow.emit("Show Toast") // Sends one-time event
    }

    /**
     * INTERVIEW BEST PRACTICE: One-Time Events (Channels)
     * Why not use SharedFlow? While SharedFlow works, a Channel is more 'robust'
     * for events that MUST be handled exactly once.
     */
    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun triggerEvent() = runBlocking {
        _eventChannel.send("Navigate to Home")
    }
}

/**
 * CONCEPT 7: Backpressure & Buffering
 * What if the producer is faster than the consumer?
 */
object BackpressureDemo {
    fun bufferDemo() = flow {
        for (i in 1..3) {
            delay(100) // Fast producer
            emit(i)
        }
    }.buffer() // Runs emission and collection in different coroutines

    fun conflateDemo() = flow {
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }.conflate() // If collector is slow, skip intermediate values and only process the latest one.
}

/**
 * CONCEPT 8: Combining Flows
 */
object CombiningFlows {
    val flow1 = flowOf("A", "B", "C").onEach { delay(100) }
    val flow2 = flowOf(1, 2, 3).onEach { delay(200) }

    // Zip: Waits for BOTH to emit (A1, B2, C3)
    fun zipDemo() = flow1.zip(flow2) { f1, f2 -> "$f1$f2" }

    // Combine: Uses latest value from either (A1, B1, B2, C2, C3)
    fun combineDemo() = flow1.combine(flow2) { f1, f2 -> "$f1$f2" }
}

/**
 * CONCEPT 9: Flattening Flows (Advanced)
 * "Flow of Flows" - Handling nested operations.
 */
object FlatteningDemo {
    fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: First")
        delay(500)
        emit("$i: Second")
    }

    // flatMapConcat: Processes one flow fully before starting next (Sequential)
    fun concat() = (1..3).asFlow().flatMapConcat { requestFlow(it) }

    // flatMapMerge: Processes multiple flows concurrently
    fun merge() = (1..3).asFlow().flatMapMerge { requestFlow(it) }

    // flatMapLatest: Cancels current flow if a new value arrives (Common for search)
    fun latest() = (1..3).asFlow().flatMapLatest { requestFlow(it) }
}

/**
 * CONCEPT 10: Channels (The "Hot" Primitive)
 * Channels are conceptually similar to BlockingQueue, but they use suspending 
 * operations instead of blocking ones.
 * 
 * Flow vs Channel:
 * - Flow: Cold, Unicast (standard Flow), declarative.
 * - Channel: Hot, Unicast (Point-to-point, even with multiple receivers), imperative.
 */
object ChannelsDemo {
    fun basicChannel() = runBlocking {
        val channel = Channel<Int>()
        
        launch {
            for (x in 1..5) {
                channel.send(x * x)
            }
            channel.close() // Important: Close to indicate end of stream
        }

        // Multiple receivers can "consume" from the same channel
        for (y in channel) {
            println("Received: $y")
        }
    }

    /**
     * CHANNEL TYPES (Interview Tip):
     * 1. Rendezvous (Default): No buffer. Sender suspends until receiver arrives.
     * 2. Buffered: Has a fixed size buffer. Sender suspends only when buffer is full.
     * 3. Unlimited: Infinite buffer (careful with memory!).
     * 4. Conflated: Keeps only the LATEST value. Sender never suspends.
     */
    val bufferedChannel = Channel<Int>(capacity = 10)
    val conflatedChannel = Channel<Int>(Channel.CONFLATED)
}