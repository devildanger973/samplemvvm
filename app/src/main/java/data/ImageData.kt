package data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 */
@Entity(tableName = "image_table")
data class ImageData(
    /**
     *
     */
    @PrimaryKey @ColumnInfo(name = "image") val imagePath1: String,
    /**
     *
     */
    val image1: Int = -1
)
