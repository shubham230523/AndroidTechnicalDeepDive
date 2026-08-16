package com.shubhamthorat.androidtechnicaldeepdive

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * ANDROID RETROFIT MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Retrofit is a type-safe HTTP client for Android and Java.
 * It turns your HTTP API into a Kotlin interface.
 */

// =========================================================================================
// PART 1: DATA MODELS (DTOs)
// =========================================================================================

data class UserRequest(val name: String, val job: String)
data class UserResponse(val id: String, val createdAt: String)

// =========================================================================================
// PART 2: THE API INTERFACE
// =========================================================================================

/**
 * Define your API endpoints here using Retrofit annotations.
 */
interface MyApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int, // Query parameter: ?page=2
        @QueryMap filters: Map<String, String> // Dynamic queries
    ): List<UserResponse>

    @GET("users/{id}")
    suspend fun getUserDetails(
        @Path("id") userId: String // Path parameter: users/123
    ): UserResponse

    @POST("users")
    suspend fun createUser(
        @Body request: UserRequest // Sending JSON in the body
    ): UserResponse

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UserRequest
    ): UserResponse

    @Headers("Cache-Control: max-age=640000") // Static Header
    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String, // Dynamic Header
        @Path("id") id: String
    ): retrofit2.Response<Unit> // Using Response wrapper for status codes
}

// =========================================================================================
// PART 3: OKHTTP CONFIGURATION (The Engine)
// =========================================================================================

/**
 * 1. Logging Interceptor: To see network logs in Logcat.
 */
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

/**
 * 2. Auth Interceptor: To automatically add Auth Tokens to every request.
 */
class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}

/**
 * 3. OkHttpClient: Configures timeouts and interceptors.
 */
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .addInterceptor(AuthInterceptor("MY_SECRET_TOKEN"))
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

// =========================================================================================
// PART 4: RETROFIT INSTANCE (The Factory)
// =========================================================================================

/**
 * Retrofit.Builder: Orchestrates the client, base URL, and converters.
 */
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://reqres.in/api/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to Kotlin Objects
    .build()

val apiService: MyApiService = retrofit.create(MyApiService::class.java)

// =========================================================================================
// PART 5: USAGE & ERROR HANDLING
// =========================================================================================

suspend fun safeApiCall() {
    try {
        val response = apiService.getUsers(page = 1, filters = emptyMap())
        // Handle success
    } catch (e: Exception) {
        // Handle network/parsing errors
        println("Error: ${e.message}")
    }
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. What is Retrofit? An abstraction layer over OkHttp that makes networking easy and type-safe.
// 2. @Query vs @Path: @Query adds parameters to URL (?key=value), @Path replaces placeholders ({id}).
// 3. What is an Interceptor? A powerful way to observe, transform, and retry network requests.
// 4. Converters: Retrofit doesn't handle JSON itself; it uses factories like Gson, Moshi, or Kotlin Serialization.
// 5. Why suspend functions? They allow non-blocking network calls that integrate perfectly with Coroutines.
// =========================================================================================
