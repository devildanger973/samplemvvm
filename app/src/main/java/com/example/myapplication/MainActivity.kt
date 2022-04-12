package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_ONE
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_TWO
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File
import java.io.FileOutputStream


/**
 *
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mHeros: MutableList<Hero>
    private lateinit var mHeroSelected: MutableList<HeroSelected>
    private lateinit var mRecyclerHero: RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    /**
     *
     */
    var arr = mutableListOf<String>()

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
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        val checkBoxAll: ImageView = findViewById(R.id.checkAll)

        var check = 1

        val count1: Int = 0
        val topAppBar: Toolbar = findViewById(R.id.image)
        mHeroAdapter = HeroAdapter(this, object : HeroAdapter.OnItemClickListener {
            override fun onItemClick(item: Hero?) {
                item?.imagePath?.let { startImageEditor(imagePath = it) }
            }

            override fun onOpenFolderClick() {
                startGalleryForResult()
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

        checkBoxAll.setOnClickListener {
            topAppBar.title = ""
            if (check == 1) {
                topAppBar.title = "Selected: 0"
                mHeroAdapter.setShowCheckBox(true)
                Log.d("checkDDDD", "${true}")
                topAppBar.navigationIcon =
                    getDrawable(androidx.navigation.ui.R.drawable.ic_mtrl_chip_close_circle)
                check = 2
            } else {
                startImageList(arr)
                topAppBar.navigationIcon =
                    getDrawable(R.drawable.ic_settings_3110)
                mHeroAdapter.setShowCheckBox(false)
                Log.d("checkDDDD", "${false}")
                check = 1
            }
        }
        mRecyclerHero.adapter = mHeroAdapter
        //mRecyclerHero.layoutManager=LinearLayoutManager(this)
        mRecyclerHero.layoutManager = gridLayoutManager

        askPermission()
        val mFloatingActionButton: FloatingActionButton = findViewById(R.id.camera)
        mFloatingActionButton.setOnClickListener() {

            takePicture.launch(null)
        }
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
    }

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
        val myIntent = Intent(this, ImageEditorActivity::class.java)
        myIntent.putExtra("FILE_PATH", imagePath)
        this.startActivity(myIntent)
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
            var a = 1

            mHeros.add(
                Hero(
                    name = "image ${a + i}",
                    imagePath = cursor?.getString(dataColumnIndex).orEmpty(),
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

    private fun saveFile(bitmap: Bitmap) {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir(filesDir.name, Context.MODE_PRIVATE)
        val file = File(directory, "fileName")
        val fos = FileOutputStream(file.absolutePath, false) // save
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        Log.d("filepath", "filepath ${file.absolutePath} length ${file.length()}")
        fos.close()
        val myIntent = Intent(this, ImageEditorActivity::class.java)
        myIntent.putExtra("FILE_PATH", file.absolutePath)
        this.startActivity(myIntent)
    }
}

