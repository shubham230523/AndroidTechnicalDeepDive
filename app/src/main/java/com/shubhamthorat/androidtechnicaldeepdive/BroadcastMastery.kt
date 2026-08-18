package com.shubhamthorat.androidtechnicaldeepdive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast

/**
 * ANDROID BROADCAST RECEIVER MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Broadcast Receivers are Android components that allow you to register for system or
 * app-level events. When an event happens, Android sends a broadcast to all registered receivers.
 *
 * KEY CONCEPTS:
 * 1. Publish-Subscribe Pattern: One sender, multiple receivers.
 * 2. System Broadcasts: Boot completed, battery low, connectivity changes.
 * 3. Custom Broadcasts: Events internal to your app or shared with other apps.
 */

// =========================================================================================
// PART 1: STATIC VS DYNAMIC RECEIVERS (CRITICAL INTERVIEW TOPIC)
// =========================================================================================

/**
 * 1. STATIC RECEIVERS:
 *    - Declared in the AndroidManifest.xml.
 *    - Can wake up your app even if it's not running.
 *    - RESTRICTION: Since Android 8.0 (Oreo), most implicit broadcasts cannot be received 
 *      via static receivers (to save battery). You MUST use dynamic receivers for them.
 */

// Example of a receiver that could be static or dynamic
class MyStaticReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // onReceive runs on the MAIN THREAD. 
        // DO NOT perform long-running tasks here (max 10 seconds before ANR).
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            println("Device Booted!")
        }
    }
}

// =========================================================================================
// PART 2: DYNAMIC REGISTRATION (MODERN APPROACH)
// =========================================================================================

/**
 * 2. DYNAMIC RECEIVERS:
 *    - Registered via code using context.registerReceiver().
 *    - Tied to a lifecycle (Activity, Fragment, or Service).
 *    - Android 14 (API 34) Requirement: You MUST specify if the receiver is 
 *      RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED.
 */
class DynamicReceiverActivity(private val context: Context) {

    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isEnabled = intent?.getBooleanExtra("state", false) ?: false
            Toast.makeText(context, "Airplane Mode: $isEnabled", Toast.LENGTH_SHORT).show()
        }
    }

    fun register() {
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 14+ requires export flags
            context.registerReceiver(
                airplaneModeReceiver, 
                filter, 
                Context.RECEIVER_NOT_EXPORTED // Only this app can send to this receiver
            )
        } else {
            context.registerReceiver(airplaneModeReceiver, filter)
        }
    }

    fun unregister() {
        // CRITICAL: Always unregister to prevent memory leaks!
        context.unregisterReceiver(airplaneModeReceiver)
    }
}

// =========================================================================================
// PART 3: SENDING CUSTOM BROADCASTS
// =========================================================================================

object BroadcastSender {
    
    const val CUSTOM_ACTION = "com.shubham.ACTION_UPDATE_DATA"

    fun send(context: Context) {
        val intent = Intent(CUSTOM_ACTION).apply {
            putExtra("data", "New Update!")
            // Recommended: Limit broadcast to your own package for security
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }

    /**
     * ORDERED BROADCASTS:
     * Sent to one receiver at a time based on priority. 
     * A receiver can "abort" the broadcast so others don't get it.
     */
    fun sendOrdered(context: Context) {
        context.sendOrderedBroadcast(Intent(CUSTOM_ACTION), null)
    }
}

// =========================================================================================
// INTERVIEW TIPS:
// =========================================================================================
/**
 * Q: Why did Android restrict static receivers in Oreo?
 * A: To prevent multiple apps from waking up simultaneously on events like CONNECTIVITY_CHANGE, 
 *    which caused significant battery drain and "thundering herd" issues.
 *
 * Q: What is the time limit for onReceive()?
 * A: 10 seconds. If you need more time, use goAsync() or start a WorkManager task.
 *
 * Q: How to communicate from a Receiver to an Activity?
 * A: Since a Receiver has a short lifecycle, it shouldn't hold a reference to an Activity.
 *    Better to use a shared Flow, LiveData, or a LocalBroadcastManager (deprecated) / Flow.
 *
 * Q: What are Export Flags in Android 14?
 * A: RECEIVER_EXPORTED (other apps can trigger it) vs RECEIVER_NOT_EXPORTED (internal only).
 *    This prevents "Intent Spoofing" where malicious apps send fake broadcasts to your app.
 */
