package com.example.myapplication

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


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
        val close: FloatingActionButton =findViewById(R.id.close)
        close.setOnClickListener(){
            finish()
        }
        val share:FloatingActionButton=findViewById(R.id.share)
        share.setOnClickListener(){
            val bmpUri = getLocalBitmapUri(mPhotograp)
            if (bmpUri != null) {
                // Construct a ShareIntent with link to image
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
                shareIntent.type = "image/png"
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            } else {
                // ...sharing failed, handle error
            }
        }
        save.setOnClickListener(){
            // get the bitmap of the view using
            // getScreenShotFromView method it is
            // implemented below
            val bitmap = getScreenShotFromView(mPhotograp)
            // if bitmap is not null then
            // save it to gallery
            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            }
            finish()
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
    fun getLocalBitmapUri(imageView: ImageView): Uri? {
        // Extract Bitmap from ImageView drawable
        val drawable = imageView.drawable
        var bmp: Bitmap? = null
        bmp = if (drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }
        // Store image to default external storage directory
        var bmpUri: Uri? = null
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png")
            val out = FileOutputStream(file.absoluteFile,false)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName().toString() + ".provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    // Method when launching drawable within Glide

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }
    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
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