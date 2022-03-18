package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_ONE
import com.example.myapplication.HeroAdapter.Companion.VIEW_TYPE_TWO


class MainActivity : AppCompatActivity() {
    private lateinit var mHeros:MutableList<Hero>
    private lateinit var  mRecyclerHero:RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter
    val PICK_IMAGE = 1
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecyclerHero=findViewById(R.id.recyclerHero)
        mHeros= mutableListOf<Hero>()
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        creatHeroList()
        mHeroAdapter= HeroAdapter(this, object: HeroAdapter.OnItemClickListener{
            override fun onItemClick(item: Hero?) {
                startImageEditor()
            }
            override fun onOpenFolderClick() {
                startGalleryForResult()

            }
        })
        mRecyclerHero.adapter=mHeroAdapter
        //mRecyclerHero.layoutManager=LinearLayoutManager(this)
        mRecyclerHero.layoutManager=gridLayoutManager
        askPermission()

        resultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

            }
        }

    }
    fun startGalleryForResult() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        resultLauncher.launch(intent)
    }
    private fun startImageEditor(){
        val myIntent = Intent(this, ImageEditorActivity::class.java)
        this.startActivity(myIntent)
    }
    private fun askPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        else {
            listAllImage()
            mHeroAdapter.setList(mHeros)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    listAllImage()
                    mHeroAdapter.setList(mHeros)
                } else {
                  //  askPermission()
                }
                return
            }
            else -> {
            }
        }
    }
    private fun listAllImage(){
        mHeros.add(Hero("",viewType = VIEW_TYPE_TWO))
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if(!isSDPresent) return
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID
//Stores all the images from the gallery in Cursor
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, orderBy
        )
//Total number of images
        val count: Int = cursor?.count ?:0

        val arrPath = arrayOfNulls<String>(count)

        for (i in 0 until count) {
            cursor?.moveToPosition(i)
            val dataColumnIndex: Int = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)?:0
            //Store the path of the image
            arrPath[i] = cursor?.getString(dataColumnIndex)
            mHeros.add(Hero(name ="image $i",imagePath = cursor?.getString(dataColumnIndex).orEmpty(), viewType = VIEW_TYPE_ONE))
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursor?.close()
    }
    private fun creatHeroList()
    {
        //mHeros.add(Hero("thor", R.drawable.thor,))
        //mHeros.add(Hero("ironmen", R.drawable.dafodil))
        //mHeros.add(Hero("hulk", R.drawable.hulk))
        //mHeros.add(Hero("spidermen", R.drawable.spiderman))

        //mHeros.add(Hero("sad", com.google.android.material.R.drawable.notification_template_icon_low_bg))

    }
}