package com.shubhamthorat.androidtechnicaldeepdive

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * ANDROID SERVICES MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * A Service is an application component that can perform long-running operations in the background.
 * IMPORTANT: A Service runs on the MAIN THREAD by default. It does NOT create its own thread.
 */

// =========================================================================================
// PART 1: STARTED SERVICE (Background Service)
// =========================================================================================

/**
 * Started Service: Triggered by startService(). Once started, it can run in the background 
 * indefinitely, even if the component that started it is destroyed.
 */
class MyStartedService : Service() {

    override fun onCreate() {
        super.onCreate()
        println("Service Created")
    }

    /**
     * onStartCommand return types:
     * 1. START_STICKY: If killed, system recreates service with a null intent. (Good for music players)
     * 2. START_NOT_STICKY: If killed, system DOES NOT recreate service.
     * 3. START_REDELIVER_INTENT: If killed, system recreates service and redelivers the last intent.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Service Started with ID: $startId")
        
        // Stop service once task is done
        // stopSelf() 

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null // Not a bound service

    override fun onDestroy() {
        super.onDestroy()
        println("Service Destroyed")
    }
}

// =========================================================================================
// PART 2: FOREGROUND SERVICE (Visible to User)
// =========================================================================================

/**
 * Foreground Service: Performs operations noticeable to the user.
 * MUST display a status bar notification.
 * Mandatory for long-running tasks in modern Android (Android 8.0+).
 */
class MyForegroundService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Working in the background...")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()

        // 1. Mandatory for Foreground Service
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}

// =========================================================================================
// PART 3: BOUND SERVICE (Client-Server Interface)
// =========================================================================================

/**
 * Bound Service: Allows components (like Activities) to bind to the service, 
 * send requests, receive responses, and perform inter-process communication (IPC).
 */
class MyBoundService : Service() {

    // 1. Binder given to clients
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MyBoundService = this@MyBoundService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    // Example method for clients
    fun getRandomNumber(): Int = (0..100).random()
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. Service vs IntentService: IntentService (Deprecated) ran on a worker thread and stopped 
//    itself. WorkManager is the modern replacement.
// 2. Service vs WorkManager: Use Service for tasks that need immediate execution or user 
//    interaction (Foreground). Use WorkManager for deferrable tasks.
// 3. Threading: A Service runs on the UI thread. For heavy tasks, you MUST spawn a new thread 
//    (using Coroutines or Executors) inside the service.
// 4. Memory Management: Android kills services when memory is low. Use START_STICKY if the 
//    task is essential.
// 5. stopSelf() vs stopService(): stopSelf() is called by the service itself; stopService() 
//    is called by an external component.
// =========================================================================================
