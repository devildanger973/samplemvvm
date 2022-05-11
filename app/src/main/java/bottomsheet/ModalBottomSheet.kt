package bottomsheet

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File


/**
 *
 */
class ModalBottomSheet(private val onAnItemClick: (FolderData) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var mHeros: MutableList<Hero>

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
        mHeros = mutableListOf()
        mFolderData = mutableListOf()
        mFolderDataAdapter =
            FolderDataAdapter(requireActivity(), object : FolderDataAdapter.OnItemClickListener {
                override fun onItemClick(item: FolderData?) {
                    //folderList(item!!.folderPath)
                    onAnItemClick.invoke(item ?: return)
                    dismiss()
                    /*Log.d("FOLDER_PATH", "$mArr")*/
                }

                override fun onOpenFolderClick() {
                }

            })
        Log.d("FOLDER_PATH", "$mHeros")

        getImageDirectories(requireActivity())
        root.findViewById<RecyclerView>(R.id.photos_grid_fo).adapter = mFolderDataAdapter
        mFolderDataAdapter.setList(mFolderData)
        return root
    }

    private fun startFolderList(folderPath: String) {
        val myIntent = Intent(requireContext(), MainActivity::class.java)
        myIntent.putExtra("FOLDER_PATH", folderPath)

    }

    companion object {
        /**
         *
         */
        const val TAG: String = "ModalBottomSheet"
    }

    private fun folderList(folderPath: String) {
        val queryUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        val selectionFolder = MediaStore.Images.Media.DATA + " like ? "
        //val f = folderPath.replace("/","//")
        val selectionargs = arrayOf("%$folderPath%")
        val cursorFolder = requireContext().contentResolver.query(
            queryUri,
            projection,
            selectionFolder,
            selectionargs,
            null
        )
        //Total number of images
        val count: Int = cursorFolder?.count ?: 0

        val arrPath = arrayOfNulls<String>(count)

        for (i in 0 until count) {
            cursorFolder?.moveToPosition(i)
            val dataColumnIndex: Int =
                cursorFolder?.getColumnIndex(MediaStore.Images.Media.DATA) ?: 0
            //Store the path of the image
            arrPath[i] = cursorFolder?.getString(dataColumnIndex)
            val a = 1

            mHeros.add(
                Hero(
                    name = "image ${a + i}",
                    imagePath = arrPath[i].orEmpty(),
                    viewType = HeroAdapter.VIEW_TYPE_ONE
                )
            )
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursorFolder?.close()
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
                            imageViewFolder = mapKey[folderPath]!!.imageViewFolder,

                            )
                    Log.d("XXXXX", "${mapKey[folderPath]}")

                } else {
                    mapKey[folderPath] = FolderData(
                        numberOfImage = 1,
                        folderPath = folderPath,
                        imageViewFolder = photoUri,
                    )
                }
                val numberImage = mapKey[folderPath]!!.numberOfImage
                val index = mFolderData.indexOf(
                    FolderData(
                        folderPath = folderPath,
                        numberOfImage = numberImage,
                        imageViewFolder = photoUri,
                    )
                )

                Log.d(
                    "Content",
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