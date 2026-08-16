package com.shubhamthorat.androidtechnicaldeepdive

import android.content.Context
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.http.GET
import javax.inject.Inject

/**
 * ANDROID OFFLINE-FIRST ARCHITECTURE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Offline-First means the app is fully functional without internet.
 * KEY PRINCIPLE: The LOCAL DATABASE is the "Single Source of Truth".
 * UI observes DB -> DB is updated from Network -> UI updates automatically.
 */

// =========================================================================================
// PART 1: THE LOCAL DATA SOURCE (Room - Single Source of Truth)
// =========================================================================================

@Entity(tableName = "posts")
data class PostEntity(@PrimaryKey val id: Int, val title: String, val body: String)

@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)
}

// =========================================================================================
// PART 2: THE REMOTE DATA SOURCE (Retrofit)
// =========================================================================================

data class PostDto(val id: Int, val title: String, val body: String)

interface PostApiService {
    @GET("posts")
    suspend fun fetchPosts(): List<PostDto>
}

// =========================================================================================
// PART 3: THE REPOSITORY (The Coordinator)
// =========================================================================================

/**
 * The Repository implements the "Offline-First" strategy.
 * It provides data from the DB and triggers a network refresh in the background.
 */
class PostRepository @Inject constructor(
    private val apiService: PostApiService,
    private val postDao: PostDao
) {
    /**
     * Logic:
     * 1. UI starts observing the flow from DB.
     * 2. Repository fetches data from Network.
     * 3. Repository saves fresh data to DB.
     * 4. UI automatically receives updates because it's observing the DB flow.
     */
    fun getPosts(): Flow<Result<List<PostEntity>>> = flow {
        emit(Result.Loading()) // 1. Emit loading state

        // 2. Emit cached data first (Fast UI response!)
        val localData = postDao.getAllPosts().first()
        emit(Result.Success(localData))

        try {
            // 3. Fetch from remote
            val remoteData = apiService.fetchPosts()
            
            // 4. Update local cache
            postDao.insertPosts(remoteData.map { PostEntity(it.id, it.title, it.body) })
            
            // 5. Re-emit from DB to ensure UI has the latest
            emitAll(postDao.getAllPosts().map { Result.Success(it) })
        } catch (e: Exception) {
            // If network fails, the user still sees the cached data!
            emit(Result.Error("Network failed, showing cached data", localData))
        }
    }
}

sealed class Result<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Result<T>(data)
    class Loading<T> : Result<T>()
    class Error<T>(message: String, data: T? = null) : Result<T>(data, message)
}

// =========================================================================================
// PART 4: HANDLING OFFLINE WRITES (WorkManager)
// =========================================================================================

/**
 * If the user performs an action (like posting a comment) while offline,
 * we use WorkManager to ensure the action is synced whenever internet returns.
 */
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Sync local changes to remote server here
        return Result.success()
    }
}

fun scheduleSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "data_sync",
        ExistingWorkPolicy.KEEP,
        syncRequest
    )
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. Single Source of Truth (SSOT): The UI never talks to the API directly. It only talks to the DB.
// 2. NetworkBoundResource: A common pattern for managing Local vs Remote data flow.
// 3. Why Offline-First? Better UX, faster load times, and saves battery/data.
// 4. Conflict Resolution: "Last Write Wins" is the simplest; "Versioning/Timestamps" is more robust.
// 5. WorkManager role: Essential for guaranteed syncing of user actions when network is restored.
// =========================================================================================
