package bottomsheet

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

/**
 *
 */
class ModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mFolderData: MutableList<FolderData>
    private lateinit var mFolderDataAdapter: FolderDataAdapter

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.content_main, container, false)
        mFolderData = mutableListOf()
        mFolderDataAdapter =
            FolderDataAdapter(requireActivity(), object : FolderDataAdapter.OnItemClickListener {
                override fun onItemClick(item: FolderData?) {
                }

                override fun onOpenFolderClick() {
                }

            })
        getImageDirectories(requireActivity())
        root.findViewById<RecyclerView>(R.id.photos_grid_fo).adapter = mFolderDataAdapter
        mFolderDataAdapter.setList(mFolderData)
        return root
    }

    companion object {
        /**
         *
         */
        const val TAG: String = "ModalBottomSheet"
    }

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
        val mapKey: HashMap<String, FolderData> = HashMap()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val photoUri = cursor.getString(cursor.getColumnIndex(projection[0]))
                val folderPath = File(photoUri).parent

                if (!directories.contains(folderPath!!)) {
                    directories.add(folderPath)
                }
                Log.d("ContentAAAAA", "${mapKey[folderPath]}")
                if (mapKey[folderPath] != null) {
                    val newValue = mapKey[folderPath]!!.numberOfImage + 1
                    mapKey[folderPath] =
                        FolderData(
                            numberOfImage = newValue,
                            folderPath = folderPath,
                            imageViewFolder = mapKey[folderPath]!!.imageViewFolder
                        )
                } else {
                    mapKey[folderPath] = FolderData(
                        numberOfImage = 1,
                        folderPath = folderPath,
                        imageViewFolder = photoUri
                    )
                }
                val numberImage = mapKey[folderPath]!!.numberOfImage
                val index = mFolderData.indexOf(
                    FolderData(
                        folderPath = folderPath,
                        numberOfImage = numberImage,
                        imageViewFolder = photoUri
                    )
                )

                Log.d(
                    "ContentAAAAA",
                    "mapKey: ${mapKey[folderPath]} index: $index folderPath: $folderPath"
                )

            } while (cursor.moveToNext())

            for ((_, FolderData) in mapKey) {
                mFolderData.add(
                    FolderData
                )
            }
        }
        return mFolderData
    }

}