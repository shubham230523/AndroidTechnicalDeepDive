package com.shubhamthorat.androidtechnicaldeepdive

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * ANDROID WORKMANAGER MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide covers WorkManager for guaranteed background execution.
 */

// =========================================================================================
// PART 1: THE WORKER (CoroutineWorker)
// =========================================================================================

/**
 * Worker: The class where you define the actual task to be performed in the background.
 * CoroutineWorker: A specialized Worker that integrates with Kotlin Coroutines.
 */
class SimpleWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 1. Get input data
        val inputName = inputData.getString("input_name") ?: "Unknown"

        return try {
            // 2. Perform the background task
            println("Working on $inputName...")
            
            // 3. Create output data
            val outputData = workDataOf("result_key" to "Task Finished Successfully")

            // 4. Return Result.success(), Result.failure(), or Result.retry()
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

// =========================================================================================
// PART 2: WORK REQUESTS (One-Time vs Periodic)
// =========================================================================================

fun scheduleWork(context: Context) {
    val workManager = WorkManager.getInstance(context)

    /**
     * Constraints: Conditions that must be met for the work to run.
     */
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // Requires internet
        .setRequiresBatteryNotLow(true)               // Don't run if battery is low
        .setRequiresCharging(false)                   // Optional: requires charger
        .build()

    /**
     * 1. OneTimeWorkRequest: Runs exactly once.
     */
    val oneTimeRequest = OneTimeWorkRequestBuilder<SimpleWorker>()
        .setConstraints(constraints)
        .setInputData(workDataOf("input_name" to "OneTimeTask"))
        .setInitialDelay(5, TimeUnit.MINUTES) // Wait before starting
        .addTag("cleanup_tag")                // Useful for canceling work
        .build()

    workManager.enqueue(oneTimeRequest)

    /**
     * 2. PeriodicWorkRequest: Runs repeatedly at intervals.
     * Minimum interval is 15 minutes.
     */
    val periodicRequest = PeriodicWorkRequestBuilder<SimpleWorker>(1, TimeUnit.HOURS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(periodicRequest)
}

// =========================================================================================
// PART 3: UNIQUE WORK (Preventing Duplicates)
// =========================================================================================

fun scheduleUniqueWork(context: Context) {
    val workManager = WorkManager.getInstance(context)

    val uniqueRequest = OneTimeWorkRequestBuilder<SimpleWorker>().build()

    /**
     * ExistingWorkPolicy:
     * - KEEP: Keep existing work, ignore new one.
     * - REPLACE: Cancel existing work, start new one.
     * - APPEND: Add new work to the end of the existing chain.
     */
    workManager.enqueueUniqueWork(
        "unique_sync_task",
        ExistingWorkPolicy.KEEP,
        uniqueRequest
    )
}

// =========================================================================================
// PART 4: WORK CHAINING (Sequential & Parallel Execution)
// =========================================================================================

fun chainWork(context: Context) {
    val workManager = WorkManager.getInstance(context)

    val workA = OneTimeWorkRequestBuilder<SimpleWorker>().addTag("A").build()
    val workB = OneTimeWorkRequestBuilder<SimpleWorker>().addTag("B").build()
    val workC = OneTimeWorkRequestBuilder<SimpleWorker>().addTag("C").build()

    // Chaining: A -> B -> C (Sequential)
    workManager.beginWith(workA)
        .then(workB)
        .then(workC)
        .enqueue()

    // Parallel: (A & B) -> C
    workManager.beginWith(listOf(workA, workB))
        .then(workC)
        .enqueue()
}

// =========================================================================================
// PART 5: OBSERVING WORK STATUS
// =========================================================================================

fun observeWork(context: Context, workId: java.util.UUID) {
    val workManager = WorkManager.getInstance(context)

    // Observe using LiveData (WorkInfo)
    workManager.getWorkInfoByIdLiveData(workId).observeForever { workInfo ->
        if (workInfo != null) {
            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> println("Work Enqueued")
                WorkInfo.State.RUNNING -> println("Work Running")
                WorkInfo.State.SUCCEEDED -> {
                    val result = workInfo.outputData.getString("result_key")
                    println("Work Finished: $result")
                }
                WorkInfo.State.FAILED -> println("Work Failed")
                WorkInfo.State.CANCELLED -> println("Work Cancelled")
                else -> {}
            }
        }
    }
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. What is WorkManager? An API for persistent, deferrable background tasks that MUST run.
// 2. When to use? For tasks that need to survive app restarts or device reboots (e.g., uploading logs).
// 3. Guaranteed Execution: WorkManager uses JobScheduler (API 23+) or AlarmManager + BroadcastReceiver.
// 4. Coroutines vs WorkManager: Use Coroutines for immediate tasks that only matter if the app is alive.
//    Use WorkManager for tasks that must finish even if the app is closed.
// =========================================================================================
