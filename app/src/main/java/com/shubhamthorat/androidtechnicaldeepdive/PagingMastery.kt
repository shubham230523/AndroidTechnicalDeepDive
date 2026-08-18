package com.shubhamthorat.androidtechnicaldeepdive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ANDROID PAGING 3 MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide covers the Paging 3 library, which helps you load and display pages of data
 * from a larger dataset from local storage or over network.
 *
 * Why Paging 3?
 * 1. In-memory caching for paged data.
 * 2. Built-in request deduplication (prevents redundant network calls).
 * 3. Configurable loading states (Handling Loading/Error/Success).
 * 4. Support for Kotlin Flow, LiveData, and RxJava.
 */

// =========================================================================================
// PART 1: THE DATA MODEL
// =========================================================================================

data class User(
    val id: Int,
    val name: String
)

// =========================================================================================
// PART 2: PAGINGSOURCE (Defining how to fetch data)
// =========================================================================================

/**
 * PagingSource is the base class for loading chunks of data.
 * It identifies the source of your data and how to retrieve it.
 */
class UserPagingSource : PagingSource<Int, User>() {

    /**
     * load() is called by the Paging library to fetch more data.
     * It runs on a background thread.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            // params.key is the current page number. If null, start from page 1.
            val currentPage = params.key ?: 1
            
            // Simulate network delay
            delay(1000)

            // Simulate fetching 20 items per page
            val items = (1..20).map { i ->
                User(id = (currentPage - 1) * 20 + i, name = "User ${(currentPage - 1) * 20 + i}")
            }

            LoadResult.Page(
                data = items,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (items.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * getRefreshKey() provides a key used for subsequent refresh calls.
     */
    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

// =========================================================================================
// PART 3: VIEWMODEL (Setting up the Pager)
// =========================================================================================

class PagingViewModel : ViewModel() {

    /**
     * Pager: The entry point for Paging 3. 
     * It combines PagingConfig and PagingSource to expose a Flow of PagingData.
     */
    val userPager: Flow<PagingData<User>> = Pager(
        config = PagingConfig(
            pageSize = 20,          // Items per page
            prefetchDistance = 2,   // Load next page when 2 items from end
            enablePlaceholders = false
        ),
        pagingSourceFactory = { UserPagingSource() }
    ).flow
        .cachedIn(viewModelScope) // CRITICAL: Prevents leaks and crashes on config changes

    /**
     * Transformations: You can use standard operators on PagingData.
     */
    val mappedUsers = userPager.map { pagingData ->
        pagingData.map { user -> user.copy(name = user.name.uppercase()) }
    }
}

// =========================================================================================
// PART 4: COMPOSE UI (Displaying the list)
// =========================================================================================

@Composable
fun UserListScreen(viewModel: PagingViewModel) {
    // Collect the Flow of PagingData as LazyPagingItems
    val users = viewModel.userPager.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = users.itemCount,
            key = { index -> users[index]?.id ?: index }
        ) { index ->
            val user = users[index]
            user?.let {
                UserItem(it)
            }
        }

        // Handle Loading States
        when (val state = users.loadState.append) {
            is LoadState.Loading -> {
                item { LoadingIndicator() }
            }
            is LoadState.Error -> {
                item { ErrorMessage(state.error.message ?: "Unknown Error") }
            }
            else -> {}
        }
        
        // Initial Refresh Loading
        when (val state = users.loadState.refresh) {
            is LoadState.Loading -> {
                item { Box(Modifier.fillParentMaxSize()) { LoadingIndicator() } }
            }
            is LoadState.Error -> {
                item { ErrorMessage(state.error.message ?: "Retry again") }
            }
            else -> {}
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = user.name, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(msg: String) {
    Text(text = "Error: $msg", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
}

// =========================================================================================
// PART 5: ADVANCED - REMOTEMEDIATOR (Offline-First)
// =========================================================================================

/**
 * RemoteMediator is used for loading data from network AND saving it to database.
 * The UI observes the database ONLY (Single Source of Truth).
 */

@Entity(tableName = "users")
data class UserEntity(@PrimaryKey val id: Int, val name: String)

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun pagingSource(): PagingSource<Int, UserEntity> // Room supports Paging 3!

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val database: RoomDatabase, // Injected
    private val apiService: Any         // Retrofit service
) : RemoteMediator<Int, UserEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // Logic to get the last item's ID or page
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    lastItem.id // Just an example
                }
            }

            // 1. Fetch from network
            // val response = apiService.getUsers(loadKey)
            
            // 2. Save to database in a transaction
            // database.withTransaction {
            //    if (loadType == LoadType.REFRESH) database.userDao().clearAll()
            //    database.userDao().insertAll(response.users)
            // }

            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

// =========================================================================================
// INTERVIEW TIPS:
// =========================================================================================
/**
 * Q: What is cachedIn(viewModelScope)?
 * A: It caches the PagingData within the ViewModel's scope. Without it, if you rotate the 
 *    screen, the PagingData would be cleared and re-fetched from the start.
 *
 * Q: Difference between PagingSource and RemoteMediator?
 * A: PagingSource handles ONE source (Network OR DB). RemoteMediator handles TWO sources
 *    (Network AND DB) to implement offline-first caching.
 *
 * Q: How to handle separators (Headers/Footers)?
 * A: Use pagingData.insertSeparators { before, after -> ... } operator in the ViewModel.
 *
 * Q: Why handle LoadState in the UI?
 * A: Users expect to see progress bars while loading and retry buttons if network fails.
 */
