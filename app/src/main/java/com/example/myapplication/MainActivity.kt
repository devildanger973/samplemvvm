package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bottomsheet.ModalBottomSheet
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_ONE
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_TWO
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import data.*
import java.io.File
import java.io.FileOutputStream


/**
 *
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mHeros: MutableList<Hero>
    private lateinit var mImageData: MutableList<ImageData>
    private lateinit var mHeroSelected: MutableList<HeroSelected>
    private lateinit var mRecyclerHero: RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter
    private lateinit var mImageListAdapter: ImageListAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    //crop
    private var loadingDialog: Dialog? = null

    /**
     *
     */
    var arr: MutableList<String> = mutableListOf()

    /**
     *
     */

    //Room - Database
    private val newWordActivityRequestCode = 1
    private val imageViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory((application as ImageApplication).repository)
    }
//Bottom Sheet
    /**
     *
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecyclerHero = findViewById(R.id.recyclerHero)
        mHeros = mutableListOf()
        mHeroSelected = mutableListOf()
        mImageData = mutableListOf()
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        val checkBoxAll: ImageView = findViewById(R.id.checkAll)
        var check = 1
        val count1 = 0
        val topAppBar: Toolbar = findViewById(R.id.image)
        mHeroAdapter = HeroAdapter(this, object : HeroAdapter.OnItemClickListener {
            override fun onItemClick(item: Hero?) {
                item?.imagePath?.let { startImageEditor(imagePath = it) }

            }

            override fun onOpenFolderClick() {
//                startGalleryForResult()
                takePicture.launch(null)
            }

            override fun onCheck(count: Int) {
                topAppBar.title = "Selected: ${count1 + count}"
            }

            override fun onSelected(array: List<String>) {
                arr = array.toMutableList()
            }
        })

        topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            topAppBar.title = ""
            topAppBar.navigationIcon =
                getDrawable(R.drawable.ic_settings_3110)
            mHeroAdapter.setShowCheckBox(false)
            arr.clear()
            check = 1
        }

        val btGallery: Button = findViewById(R.id.gallery)
        val btEdited: Button = findViewById(R.id.edited)
        val btEditedHide: Button = findViewById(R.id.editedHide)

        checkBoxAll.setOnClickListener {
            topAppBar.title = ""
            if (check == 1) {
                topAppBar.title = "Selected: 0"
                mHeroAdapter.setShowCheckBox(true)
                Log.d("checkDDDD", "${true}")
                topAppBar.navigationIcon =
                    getDrawable(androidx.navigation.ui.R.drawable.ic_mtrl_chip_close_circle)
                btEditedHide.visibility = View.VISIBLE
                btEdited.visibility = View.GONE
                check = 2
            } else {
                if (arr.size == 0) {
                    topAppBar.navigationIcon =
                        getDrawable(R.drawable.ic_settings_3110)
                    mHeroAdapter.setShowCheckBox(false)
                    Log.d("checkDDDD", "${false}")
                    check = 1
                    arr.clear()
                    btEdited.visibility = View.VISIBLE
                    btEditedHide.visibility = View.GONE
                } else {
                    startImageList(arr)
                    mHeroAdapter.setShowCheckBox(false)
                    topAppBar.navigationIcon =
                        getDrawable(R.drawable.ic_settings_3110)
                    check = 1
                    arr.clear()
                    btEdited.visibility = View.VISIBLE
                    btEditedHide.visibility = View.GONE
                }
            }
        }
        mRecyclerHero.adapter = mHeroAdapter
        //mRecyclerHero.layoutManager=LinearLayoutManager(this)
        mRecyclerHero.layoutManager = gridLayoutManager

        askPermission()
//        val mFloatingActionButton: FloatingActionButton = findViewById(R.id.camera)
//        mFloatingActionButton.setOnClickListener() {
//
//            takePicture.launch(null)
//        }
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val myIntent = Intent(this, ImageEditorActivity::class.java)
                    myIntent.putExtra("FILE_PATH", data?.data.toString())
                    this.startActivity(myIntent)
                }
            }
        startFromNotification()


        //Room - Database
        mImageData = mutableListOf()
        mImageListAdapter = ImageListAdapter(this, object : ImageListAdapter.OnItemClickListener {
            override fun onItemClick(item: ImageData) {
                startImageEditor(imagePath = item.imagePath1)
            }
        })
        imageViewModel.allImage.observe(this) { image ->
            // Update the cached copy of the words in the adapter.
            image.let { mImageListAdapter.setList(it as MutableList<ImageData>) }
            Log.d("CCCCC", "$image")
            // Select icon edited
            for ((imagePath1) in image) {
                for (b in mHeros) {
                    if (imagePath1 == b.imagePath) {
                        b.isEdited = true
                        mHeroAdapter.setShowEdited(true)
                    }
                }
            }
            mHeroAdapter.setList(mHeros)
        }
        var show = false
        btEdited.setOnClickListener {
            mRecyclerHero.adapter = mImageListAdapter
            show = true
        }
        btGallery.setOnClickListener {
            if (show) {
                mHeros.clear()
                listAllImage()
                mHeroAdapter.setList(mHeros)
                mRecyclerHero.adapter = mHeroAdapter
                show = false
            } else {
                mHeros.clear()
                val modalBottomSheet = ModalBottomSheet() { folder ->
                    folderList(folder.folderPath)
                    mRecyclerHero.adapter = mHeroAdapter
                    mHeroAdapter.setList(mHeros)
                    Log.d("FOLDER_PATH1", "${folder.folderPath}")
                }
                modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
            }
        }

    }

    private fun folderList(folderPath: String) {
        val queryUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        val selectionFolder = MediaStore.Images.Media.DATA + " like ? "
        //val f = folderPath.replace("/","//")
        val selectionargs = arrayOf("$folderPath%")
        val cursorFolder = this.contentResolver.query(
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
                    viewType = VIEW_TYPE_ONE
                )
            )
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursorFolder?.close()
    }

    /**
     *
     */
    //Room - Database
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {

            intentData?.getStringExtra("EXTRA_REPLY")?.let { reply ->
                val image = ImageData(reply)
                imageViewModel.insert(image)

            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     *
     */
    fun tokenListener() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            // Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    /**
     *
     */
    private fun startFromNotification() {
        val boolean = intent.getBooleanExtra("START_FROM_NOTI", false)
        if (boolean) {
            val myIntent = Intent(this, ImageEditorActivity::class.java)
            val path = intent.getStringExtra("FILE_PATH")
            myIntent.putExtra("FILE_PATH", path)
            this.startActivity(myIntent)
            Log.d("check22222", " android > Q $path")

        }
    }

    /**
     *
     */
    fun startGalleryForResult() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun startImageEditor(imagePath: String) {
        val myIntent = Intent(this@MainActivity, ImageEditorActivity::class.java)
        myIntent.putExtra("FILE_PATH", imagePath)
        startActivityForResult(myIntent, newWordActivityRequestCode)
    }


    private fun startImageList(imagePath: List<String>) {
        val myIntent = Intent(this, ImageEditorActivity::class.java)

        myIntent.putStringArrayListExtra("LIST", imagePath as ArrayList<String?>?)
        this.startActivity(myIntent)
    }

    private fun askPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA

                ),
                1
            )
        } else {
            listAllImage()
            mHeroAdapter.setList(mHeros)
        }
    }

    /**
     *
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    listAllImage()
                    mHeroAdapter.setList(mHeros)
                } else {
                    //askPermission()
                }
                return
            }
            else -> {
            }
        }
    }


    private fun listAllImage() {
        mHeros.add(Hero("", viewType = VIEW_TYPE_TWO))
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if (!isSDPresent) return
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID
//Stores all the images from the gallery in Cursor
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, orderBy
        )
//Total number of images
        val count: Int = cursor?.count ?: 0

        val arrPath = arrayOfNulls<String>(count)

        for (i in 0 until count) {
            cursor?.moveToPosition(i)
            val dataColumnIndex: Int = cursor?.getColumnIndex(MediaStore.Images.Media.DATA) ?: 0
            //Store the path of the image
            arrPath[i] = cursor?.getString(dataColumnIndex)
            val a = 1

            mHeros.add(
                Hero(
                    name = "image ${a + i}",
                    imagePath = arrPath[i].orEmpty(),
                    viewType = VIEW_TYPE_ONE
                )
            )
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursor?.close()
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            saveFile(bitmap)
        }

    private fun saveFile(bitmap: Bitmap?) {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir(filesDir.name, Context.MODE_PRIVATE)
        val file = File(directory, "fileName")
        val fos = FileOutputStream(file.absolutePath, false) // save
        (bitmap ?: return).compress(Bitmap.CompressFormat.JPEG, 100, fos)
        Log.d("filepath", "filepath ${file.absolutePath} length ${file.length()}")
        fos.close()
        val myIntent = Intent(this, ImageEditorActivity::class.java)
        myIntent.putExtra("FILE_PATH", file.absolutePath)
        this.startActivity(myIntent)
    }
}

