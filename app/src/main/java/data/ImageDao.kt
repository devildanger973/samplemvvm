package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 *
 */
@Dao
interface ImageDao {

    /**
     *
     */// The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM image_table ORDER BY image ASC")
    fun getAlphabetizedImage(): Flow<List<ImageData>>

    /**
     *
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(imageData: ImageData)

    /**
     *
     */
    @Query("DELETE FROM image_table")
    suspend fun deleteAll()
}