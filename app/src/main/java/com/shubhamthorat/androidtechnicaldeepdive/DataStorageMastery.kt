package com.shubhamthorat.androidtechnicaldeepdive

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

/**
 * ANDROID DATASTORE & SHAREDPREFERENCES MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide covers the evolution of local data storage in Android:
 * 1. SharedPreferences (The Legacy)
 * 2. Preferences DataStore (The Modern Key-Value)
 * 3. Proto DataStore (The Typed Storage)
 */

// =========================================================================================
// PART 1: SHAREDPREFERENCES (LEGACY)
// =========================================================================================

/**
 * SharedPreferences is the old way to store small key-value pairs.
 * 
 * INTERVIEW ALERT: Why is it bad?
 * 1. It blocks the UI thread on fsync() calls.
 * 2. No error handling (crashes on parsing errors).
 * 3. No support for Flow/Coroutines.
 */
class LegacyStorage(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveData(name: String, age: Int) {
        prefs.edit().apply {
            putString("name", name)
            putInt("age", age)
            
            // apply() vs commit():
            // apply() is asynchronous and safe for UI thread.
            // commit() is synchronous and returns a boolean (blocks UI thread).
            apply() 
        }
    }

    fun getName(): String? = prefs.getString("name", null)
}

// =========================================================================================
// PART 2: PREFERENCES DATASTORE (MODERN KEY-VALUE)
// =========================================================================================

// Extension property to create DataStore instance (Singleton)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Preferences DataStore uses Coroutines and Flow to store data asynchronously.
 */
class UserSettingsManager(private val context: Context) {

    // Keys must be defined using specific type functions
    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_NIGHT_MODE = booleanPreferencesKey("is_night_mode")
    }

    // Reading data: Returns a Flow
    val userNameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: "Guest"
        }
        .catch { exception ->
            // DataStore throws IOException when there's an error reading data
            emit("Error")
        }

    // Writing data: Uses edit() which is a suspend function
    suspend fun updateName(newName: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USER_NAME] = newName
        }
    }
}

// =========================================================================================
// PART 3: PROTO DATASTORE (TYPED OBJECT STORAGE)
// =========================================================================================

/**
 * Proto DataStore stores custom data types with type safety using Protocol Buffers.
 * (For this example, we use Kotlin Serialization to simulate the behavior).
 */

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val isPremium: Boolean = false
)

object UserProfileSerializer : Serializer<UserProfile> {
    override val defaultValue: UserProfile = UserProfile()

    override suspend fun readFrom(input: InputStream): UserProfile {
        return try {
            Json.decodeFromString(
                deserializer = UserProfile.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserProfile, output: OutputStream) {
        output.write(Json.encodeToString(UserProfile.serializer(), t).toByteArray())
    }
}

// =========================================================================================
// PART 4: MIGRATION (SharedPreferences -> DataStore)
// =========================================================================================

/**
 * DataStore provides a built-in way to migrate legacy SharedPreferences.
 */
fun provideDataStoreWithMigration(context: Context): DataStore<Preferences> {
    return preferencesDataStore(
        name = "settings",
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(context, "user_prefs")
            )
        }
    ).getValue(context, ::dataStore) // Simplified for explanation
}

// =========================================================================================
// INTERVIEW COMPARISON TABLE & TIPS
// =========================================================================================

/**
 * FEATURE          | SHAREDPREFERENCES | DATASTORE
 * -----------------|-------------------|-------------------
 * Async API        | No (only listeners)| Yes (Flow)
 * Main-Safety      | No                | Yes
 * Error Handling   | No                | Yes
 * Type Safety      | No                | Yes (Proto)
 * Transactional    | No                | Yes
 * 
 * INTERVIEW TIPS:
 * 1. "Does SharedPreferences block the main thread?" -> Yes, during sync/commit.
 * 2. "How does DataStore handle errors?" -> It uses standard IO exception handling within Flow's catch block.
 * 3. "When to use Proto vs Preferences DataStore?" -> Use Preferences for simple key-value; 
 *    use Proto when you have complex data models requiring type safety.
 * 4. "Is DataStore a replacement for Room?" -> No. DataStore is for small settings; 
 *    Room is for large, relational datasets.
 */
