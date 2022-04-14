package data

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 *
 */
class ImageApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process

    /**
     *
     */
    private val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())

    /**
     *
     */// Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database: ImageRoomDatabase by lazy {
        ImageRoomDatabase.getDatabase(
            this,
            applicationScope
        )
    }

    /**
     *
     */
    val repository: ImageRepository by lazy { ImageRepository(database.imageDao()) }
}