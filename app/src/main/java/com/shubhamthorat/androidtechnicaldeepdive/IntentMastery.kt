package com.shubhamthorat.androidtechnicaldeepdive

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

/**
 * ANDROID INTENTS & DEEP LINKING MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * An Intent is a messaging object you can use to request an action from another app component.
 *
 * KEY CONCEPTS:
 * 1. Explicit Intents: Specify the exact component to start (internal).
 * 2. Implicit Intents: Specify a general action; the system finds a matching component.
 * 3. PendingIntents: A token that you give to a foreign application (e.g. Notification Manager), 
 *    which allows that application to use your application's permissions to execute a predefined Intent.
 */

// =========================================================================================
// PART 1: EXPLICIT VS IMPLICIT INTENTS
// =========================================================================================

class IntentDemos(private val context: Context) {

    /**
     * Explicit Intent: Used to navigate within your own app.
     */
    fun startDetailsActivity(userId: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        context.startActivity(intent)
    }

    /**
     * Implicit Intent: Asking the system to perform an action.
     */
    fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        
        // INTERVIEW TIP: Always check if there is an app to handle the intent to avoid crash.
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context.startActivity(intent)
    }
}

// =========================================================================================
// PART 2: PENDING INTENTS (MODERN REQUIREMENTS)
// =========================================================================================

/**
 * Since Android 12 (API 31), you MUST specify mutability flags.
 */
object PendingIntentFactory {

    fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // FLAG_IMMUTABLE: The intent cannot be modified by the foreign app.
            // FLAG_MUTABLE: Allows the foreign app to modify the intent (rarely used).
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            flags
        )
    }
}

// =========================================================================================
// PART 3: DEEP LINKING VS APP LINKS
// =========================================================================================

/**
 * DEEP LINKS (Custom Schemes):
 * Example: myapp://profile/123
 * - Can be handled by multiple apps (shows chooser).
 * - Defined in AndroidManifest with <intent-filter>.
 *
 * APP LINKS (Verified HTTP Links):
 * Example: https://www.myapp.com/profile/123
 * - Verified using Digital Asset Links (JSON file on your server).
 * - Opens your app DIRECTLY without a chooser.
 */

class DeepLinkHandler(private val activity: Activity) {

    fun handleIncomingIntent() {
        val intent = activity.intent
        val action: String? = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            // Example URI: https://www.myapp.com/profile/shubham
            val userId = data.lastPathSegment
            println("Loading profile for user: $userId")
        }
    }
}

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: What is the difference between Intent.FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TOP?
 * A: NEW_TASK starts the activity in a new task (standard for notifications).
 *    CLEAR_TOP closes all activities above the target activity and brings it to the top.
 *
 * Q: How to pass large data between activities?
 * A: DO NOT pass large bitmaps or lists in Intent extras (limit is ~1MB, risk TransactionTooLargeException).
 *    Better to use a shared ViewModel, a singleton repository, or local storage.
 *
 * Q: What are "Verified App Links"?
 * A: They are HTTP URLs that have been verified by a website owner to open in their app. 
 *    Requires an 'assetlinks.json' file under '.well-known/' on the web server.
 *
 * Q: What is Intent Spoofing?
 * A: It's a security risk where a malicious app sends an Intent to an exported component 
 *    in your app to perform unauthorized actions. Fix: Set 'exported="false"' or use custom permissions.
 */
