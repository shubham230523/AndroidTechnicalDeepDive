package com.shubhamthorat.androidtechnicaldeepdive

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * ANDROID ROOM DATABASE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide covers Room persistence library from basics to advanced relationships and migrations.
 */

// =========================================================================================
// PART 1: ENTITIES (The Tables)
// =========================================================================================

/**
 * @Entity: Defines a table in SQLite.
 * 'tableName' is optional; defaults to class name.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "full_name") val name: String,
    val age: Int,
    @Ignore val tempProfilePic: String? = null // Room ignores this field
)

/**
 * Embedded: Allows decomposing a complex object into columns in the same table.
 */
data class Address(
    val city: String,
    val street: String
)

@Entity
data class UserProfile(
    @PrimaryKey val profileId: Int,
    @Embedded val address: Address // City and Street become columns in UserProfile table
)

// =========================================================================================
// PART 2: DATA ACCESS OBJECT (DAO)
// =========================================================================================

/**
 * @Dao: Defines the interface for database interactions.
 * Room generates the implementation at compile time.
 */
@Dao
interface UserDao {
    // Suspend for Coroutines (background thread)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Flow for reactive updates (automatically triggers when data changes)
    @Query("SELECT * FROM users ORDER BY full_name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?
}

// =========================================================================================
// PART 3: TYPE CONVERTERS
// =========================================================================================

/**
 * Used for types Room doesn't know how to store (like Date or custom Objects).
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// =========================================================================================
// PART 4: RELATIONSHIPS (1:1, 1:N, N:M)
// =========================================================================================

// 1. One-to-One Relationship
@Entity
data class Library(
    @PrimaryKey val libraryId: Long,
    val name: String
)

@Entity
data class Librarian(
    @PrimaryKey val librarianId: Long,
    val libraryOwnerId: Long // Foreign Key
)

data class LibraryWithLibrarian(
    @Embedded val library: Library,
    @Relation(
        parentColumn = "libraryId",
        entityColumn = "libraryOwnerId"
    )
    val librarian: Librarian
)

// 2. One-to-Many Relationship
@Entity
data class Playlist(
    @PrimaryKey val playlistId: Long,
    val userCreatorId: Long
)

@Entity
data class Song(
    @PrimaryKey val songId: Long,
    val playlistOwnerId: Long
)

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "playlistOwnerId"
    )
    val songs: List<Song>
)

// =========================================================================================
// PART 5: DATABASE CLASS & SINGLETON
// =========================================================================================

/**
 * @Database: The main entry point.
 * entities: List all tables.
 * version: Must increment when schema changes.
 */
@Database(
    entities = [User::class, UserProfile::class, Library::class, Librarian::class, Playlist::class, Song::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2) // Adding migration logic
                .build()
                INSTANCE = instance
                instance
            }
        }

        // =========================================================================================
        // PART 6: MIGRATIONS
        // =========================================================================================
        /**
         * Manual migration from version 1 to 2.
         * Useful when you add a column or a new table.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Example: Adding a new column to the users table
                db.execSQL("ALTER TABLE users ADD COLUMN bio TEXT DEFAULT ''")
            }
        }
    }
}

// =========================================================================================
// INTERVIEW TIPS:
// 1. Room is an abstraction layer over SQLite.
// 2. Why Room? Compile-time verification of SQL, less boilerplate, Coroutine/Flow support.
// 3. What is @Volatile? Ensures changes to INSTANCE are visible to all threads immediately.
// 4. Why use a Singleton for Database? Database instances are expensive.
// =========================================================================================
