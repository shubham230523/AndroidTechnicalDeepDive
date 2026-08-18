package com.shubhamthorat.androidtechnicaldeepdive

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

/**
 * ANDROID CONTENT PROVIDER MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Content Providers manage access to a central repository of data. They wrap the data
 * and provide mechanisms for defining data security.
 *
 * KEY CONCEPTS:
 * 1. Data Sharing: It is the standard interface that connects data in one process with 
 *    code running in another process.
 * 2. URI (Uniform Resource Identifier): Every piece of data is identified by a URI.
 * 3. ContentResolver: The client-side object used to communicate with the Provider.
 */

// =========================================================================================
// PART 1: THE CONTENT PROVIDER IMPLEMENTATION
// =========================================================================================

/**
 * A Content Provider must override 6 key methods.
 */
class MyUserProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.shubham.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/users")

        // URI Matcher IDs
        private const val USERS = 1
        private const val USER_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            // content://com.shubham.provider/users
            addURI(AUTHORITY, "users", USERS)
            // content://com.shubham.provider/users/5
            addURI(AUTHORITY, "users/#", USER_ID)
        }
    }

    /**
     * 1. onCreate(): Initialize the provider. Called on the MAIN THREAD.
     * Keep it fast; defer heavy initialization (like DB creation) if possible.
     */
    override fun onCreate(): Boolean {
        return true
    }

    /**
     * 2. query(): Retrieve data. Returns a Cursor.
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(arrayOf("_id", "name"))
        
        when (uriMatcher.match(uri)) {
            USERS -> {
                cursor.addRow(arrayOf(1, "Shubham"))
                cursor.addRow(arrayOf(2, "Technical Deep Dive"))
            }
            USER_ID -> {
                val id = uri.lastPathSegment
                cursor.addRow(arrayOf(id, "Specific User"))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        
        // Notify the resolver that the data changed (for reactive UI)
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    /**
     * 3. insert(): Add new data. Returns URI of the new record.
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Logic to insert into DB
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(uri, "3") 
    }

    /**
     * 4. update(): Modify existing data.
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 1 // Number of rows updated
    }

    /**
     * 5. delete(): Remove data.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 1 // Number of rows deleted
    }

    /**
     * 6. getType(): Return the MIME type of the data.
     * Very important for Intent resolution.
     */
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            USERS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.user"
            USER_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.user"
            else -> null
        }
    }
}

// =========================================================================================
// PART 2: THE CONTENT RESOLVER (CLIENT SIDE)
// =========================================================================================

class UserDataManager(private val context: Context) {

    fun fetchUsers() {
        val uri = MyUserProvider.CONTENT_URI
        val cursor = context.contentResolver.query(
            uri, null, null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                println("User: $name")
            }
        }
    }
}

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: Why use a Content Provider instead of a Shared DB?
 * A: Security and Abstraction. Other apps don't need to know about your DB schema, 
 *    and you can control exactly what data they see via permissions.
 *
 * Q: Which thread does onReceive() / query() run on?
 * A: onCreate() runs on Main Thread. However, query/insert/update/delete are 
 *    invoked from the Binder thread pool, meaning they must be THREAD-SAFE.
 *
 * Q: What is the URI structure?
 * A: content://[authority]/[path]/[id]
 *    - content:// is the scheme.
 *    - authority is the unique string (usually package name).
 *    - path is the table/data type.
 *    - id is the specific record (optional).
 *
 * Q: What are MIME types in Providers?
 * A: - Dir (directory): Multiple items. Starts with "vnd.android.cursor.dir".
 *    - Item: Single item. Starts with "vnd.android.cursor.item".
 */
