package data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 *
 */
class ImageRepository(private val ImageDao: ImageDao) {
    /**
     *
     */
    val allImage: Flow<List<ImageData>> = ImageDao.getAlphabetizedImage()

    /**
     *
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(imageData: ImageData) {
        ImageDao.insert(imageData)
    }
}