package com.example.myapplication.edit.select.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.edit.select.model.ImgDt
import data.ImageData
import kotlinx.coroutines.flow.Flow

/**
 *
 */
@Dao
interface ImgDao {

    /**
     *
     */// The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM image_table ORDER BY image ASC")
    fun getAlphabetizedImage(): Flow<List<ImgDt>>
 //tesst commit
    /**
     *
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(imgDt: ImgDt)

    /**
     *
     */
    @Query("DELETE FROM image_table")
    suspend fun deleteAll()
}