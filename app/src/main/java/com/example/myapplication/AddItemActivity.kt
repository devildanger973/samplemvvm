package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_ONE


/**
 *
 */
class AddItemActivity : AppCompatActivity() {
    private lateinit var mHeros: MutableList<Hero>
    private lateinit var mHeroSelected: MutableList<HeroSelected>
    private lateinit var mRecyclerHero: RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter

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
        setContentView(R.layout.add_item_activity)
        mRecyclerHero = findViewById(R.id.recyclerHero1)
        mHeros = mutableListOf()
        mHeroSelected = mutableListOf()
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        val checkBoxAll: ImageView = findViewById(R.id.checkAll1)
        val count1: Int = 0
        val topAppBar: Toolbar = findViewById(R.id.image1)
        mHeroAdapter = HeroAdapter(this, object : HeroAdapter.OnItemClickListener {
            override fun onItemClick(item: Hero?) {
            }

            override fun onOpenFolderClick() {
            }

            override fun onCheck(count: Int) {
                topAppBar.title = "Selected: ${count1 + count}"
            }

            override fun onSelected(array: List<String>) {
                arr = array.toMutableList()
            }
        })
        topAppBar.navigationIcon =
            getDrawable(androidx.navigation.ui.R.drawable.ic_mtrl_chip_close_circle)
        topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            topAppBar.title = ""
            finish()
        }
        mHeroAdapter.setShowCheckBox(true)
        checkBoxAll.setOnClickListener {
            topAppBar.title = "Selected: 0"
            startImageList(arr)
            Log.d("ASASAS", "$arr")
        }
        mRecyclerHero.adapter = mHeroAdapter
        //mRecyclerHero.layoutManager=LinearLayoutManager(this)
        mRecyclerHero.layoutManager = gridLayoutManager
        listAllImage()
        mHeroAdapter.setList(mHeros)

        comparison()
    }

    private fun comparison() {
        val list = intent.getStringArrayListExtra("LIST_SELECTED")
        if (list != null) {

            for (item in list) {
                for (item1 in mHeros) {
                    if (item == item1.imagePath) {
                        item1.isSelected = true
                    }
                }
            }
            mHeroAdapter.setList(mHeros)
        }
    }

    private fun startImageList(imagePath: List<String>) {
        val myIntent = Intent(this, ImageEditorActivity::class.java)
        myIntent.putStringArrayListExtra("LIST_ITEM", imagePath as ArrayList<String?>?)
        myIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        this.startActivity(myIntent)
        finish()
    }

    private fun listAllImage() {
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
}

