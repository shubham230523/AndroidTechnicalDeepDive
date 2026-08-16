package com.shubhamthorat.androidtechnicaldeepdive

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * ANDROID HILT MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide covers Dependency Injection (DI) with Hilt from basics to advanced.
 */

// =========================================================================================
// PART 1: SETUP & APPLICATION CLASS
// =========================================================================================

/**
 * 1. @HiltAndroidApp: MUST be added to your Application class.
 * It triggers Hilt's code generation and provides the base container for the app.
 */
@HiltAndroidApp
class BaseApplication : Application()

// =========================================================================================
// PART 2: BASIC CONSTRUCTOR INJECTION
// =========================================================================================

/**
 * 2. @Inject: Used on a constructor to tell Hilt how to create an instance.
 * Classes injected this way don't need a @Module if all their parameters are also provided.
 */
class AnalyticsService @Inject constructor() {
    fun trackEvent(name: String) {
        println("Tracking: $name")
    }
}

// =========================================================================================
// PART 3: HILT MODULES (@Provides vs @Binds)
// =========================================================================================

interface Logger {
    fun log(msg: String)
}

class ConsoleLogger @Inject constructor() : Logger {
    override fun log(msg: String) = println("LOG: $msg")
}

/**
 * 3. @Module: Tells Hilt how to provide instances of interfaces or classes you don't own (like Retrofit).
 * @InstallIn: Specifies the lifetime of the dependencies (SingletonComponent = App lifetime).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {

    /**
     * @Binds: Used for interfaces. It's more efficient than @Provides.
     * The implementation must have an @Inject constructor.
     */
    @Binds
    @Singleton // Scopes this to be a singleton
    abstract fun bindLogger(impl: ConsoleLogger): Logger
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * @Provides: Used when you need to perform initialization logic or for external libraries.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): String { // Dummy example
        return "OkHttpClientInstance"
    }
}

// =========================================================================================
// PART 4: QUALIFIERS (Handling Multiple Implementations)
// =========================================================================================

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FileLogger

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseLogger

// Usage:
// @Module
// @InstallIn(SingletonComponent::class)
// object MultiLoggerModule {
//     @FileLogger @Provides fun provideFileLogger(): Logger = ...
//     @DatabaseLogger @Provides fun provideDbLogger(): Logger = ...
// }

// =========================================================================================
// PART 5: ANDROID ENTRY POINTS & VIEWMODELS
// =========================================================================================

/**
 * 4. @HiltViewModel: Identifies a ViewModel for injection.
 * Hilt automatically uses the ViewModelFactory internally.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val logger: Logger,
    @ApplicationContext private val context: Context // Hilt provides context via qualifiers
) : ViewModel() {
    fun doWork() {
        logger.log("ViewModel is working with context: $context")
    }
}

/**
 * 5. @AndroidEntryPoint: Added to Activities/Fragments to enable injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Field injection: Used in classes that Hilt instantiates (like Activities)
    @Inject lateinit var analytics: AnalyticsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.trackEvent("AppLaunched")
        
        setContent {
            Text("Hilt Mastery is Active!")
        }
    }
}

// =========================================================================================
// PART 6: ASSISTED INJECTION (Dynamic Parameters)
// =========================================================================================

/**
 * Use @AssistedInject when you need a dependency AND a runtime parameter (e.g., an ID).
 */
class ProductManager @AssistedInject constructor(
    private val logger: Logger,
    @Assisted val productId: String
) {
    @AssistedFactory
    interface Factory {
        fun create(productId: String): ProductManager
    }
}

// =========================================================================================
// PART 7: ENTRY POINTS (Accessing Hilt in non-Hilt classes)
// =========================================================================================

/**
 * Used for ContentProviders or external SDKs where Hilt doesn't support @AndroidEntryPoint.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltEntryPoint {
    fun getLogger(): Logger
}

fun manualAccess(context: Context) {
    val hiltEntryPoint = EntryPoints.get(context.applicationContext, HiltEntryPoint::class.java)
    val logger = hiltEntryPoint.getLogger()
    logger.log("Accessed Hilt manually!")
}
