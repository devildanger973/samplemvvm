package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max


class ImageEditorActivity : AppCompatActivity() {
    private lateinit var mHeros:MutableList<Hero>
    private lateinit var  mRecyclerList: RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val filePath = intent.getStringExtra("FILE_PATH")
        val mPhotograp: ImageView = findViewById(R.id.image_view)
        val uri: Uri
        uri = Uri.parse(filePath)
        mPhotograp.setImageURI(uri)
        val save:Button=findViewById(R.id.save)
        save.setOnClickListener(){

            val myIntent = Intent(this, MainActivity::class.java)
            this.startActivity(myIntent)
        }
        mRecyclerList=findViewById(R.id.recyclerList)
        mHeros= mutableListOf<Hero>()
        mHeroAdapter= HeroAdapter(this, object: HeroAdapter.OnItemClickListener{
            override fun onItemClick(item: Hero?) {
                val uriItem: Uri= Uri.parse(item?.imagePath)
                mPhotograp.setImageURI(uriItem)
            }
            override fun onOpenFolderClick() {
                startGalleryForResult()
            }
        })
        mRecyclerList.adapter=mHeroAdapter
        mRecyclerList.layoutManager=LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        resultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val uriGallery: Uri = Uri.parse(data?.data.toString())
                mPhotograp.setImageURI(uriGallery)
            }
        }
        listAllImage()
        mHeroAdapter.setList(mHeros)

    }

    fun startGalleryForResult() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }
    private fun listAllImage(){
        mHeros.add(Hero("",viewType = HeroAdapter.VIEW_TYPE_TWO))
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
            mHeros.add(Hero(
                name ="",
                imagePath = cursor?.getString(dataColumnIndex).orEmpty()
                ,viewType = HeroAdapter.VIEW_TYPE_ONE
            ))
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursor?.close()
    }
}