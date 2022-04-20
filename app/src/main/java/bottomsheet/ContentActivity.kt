package bottomsheet

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import java.io.File

/**
 *
 */
class ContentActivity : AppCompatActivity() {
    private lateinit var mFolderData: MutableList<FolderData>
    private lateinit var mFolderDataAdapter: FolderDataAdapter
    private lateinit var mRecyclerFolder: RecyclerView

    /**
     *
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        mFolderData = mutableListOf()
        mRecyclerFolder = findViewById(R.id.photos_grid_fo)
        mFolderDataAdapter =
            FolderDataAdapter(this, object : FolderDataAdapter.OnItemClickListener {
                override fun onItemClick(item: FolderData?) {
                }

                override fun onOpenFolderClick() {
                }

            })
        getImageDirectories(this)
        mRecyclerFolder.adapter = mFolderDataAdapter
        mFolderDataAdapter.setList(mFolderData)
    }

    /**
     *
     */
    private fun getImageDirectories(mContext: Context): MutableList<FolderData> {
        val directories: ArrayList<String> = ArrayList()
        val contentResolver: ContentResolver = mContext.contentResolver
        val queryUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        val includeImages = MediaStore.Images.Media.MIME_TYPE + " LIKE 'image/%' "
        val excludeGif =
            " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/gif' " + " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/giff' "
        val selection = includeImages + excludeGif
        val cursor: Cursor? = contentResolver.query(queryUri, projection, selection, null, null)
        val mapKey: HashMap<String, Int> = HashMap()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val photoUri = cursor.getString(cursor.getColumnIndex(projection[0]))
                val folderPath = File(photoUri).parent

                if (!directories.contains(folderPath!!)) {
                    directories.add(folderPath)
                }
                Log.d("ContentAAAAA", "${mapKey[folderPath]}")
                if (mapKey[folderPath] != null) {
                    val newValue = mapKey[folderPath]!! + 1
                    mapKey[folderPath] = newValue
                } else {
                    mapKey.put(folderPath, 1)
                }
                val numberImage = when (mapKey[folderPath]) {
                    null -> 1
                    else -> mapKey[folderPath] ?: 1
                }
                val index = mFolderData.indexOf(
                    FolderData(
                        folderPath = folderPath,
                        numberOfImage = numberImage,
                        imageViewFolder = ""
                    )
                )

                Log.d(
                    "ContentAAAAA",
                    "mapKey: ${mapKey[folderPath]} index: $index folderPath: $folderPath"
                )

            } while (cursor.moveToNext())
            val imageViewFolder = mFolderData.first().folderPath
            val uri: Uri = Uri.parse(imageViewFolder)
            for ((folderPath, i: Int) in mapKey) {
                mFolderData.add(
                    FolderData(
                        folderPath = folderPath,
                        numberOfImage = i,
                        imageViewFolder = uri.toString()
                    )
                )
            }
        }
        return mFolderData
    }
}

