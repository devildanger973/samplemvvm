package com.example.myapplication

import android.app.Activity
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


/**
 *
 */
class ImageEditorActivity : AppCompatActivity() {
    private lateinit var mHeros: MutableList<Hero>
    private lateinit var mRecyclerList: RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val id: String = "my_channel_01"

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val filePath = intent.getStringExtra("FILE_PATH")
        val mPhotograp: ImageView = findViewById(R.id.image_view)
        val uri: Uri = Uri.parse(filePath)
        mPhotograp.setImageURI(uri)
        val save: Button = findViewById(R.id.save)
        val close: Button = findViewById(R.id.close)
        close.setOnClickListener() {
            finish()
        }
        val share: Button = findViewById(R.id.share)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        share.setOnClickListener() {
            progressBar.visibility = View.VISIBLE
            var i = progressBar.progress
            Thread(Runnable {
                // this loop will run until the value of i becomes 99
                while (i < 100) {
                    i += 1
                    // Update the progress bar and display the current value
                    val handler: Handler = Handler()
                    handler.post(Runnable {
                        progressBar.progress = i
                    })
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
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
                // setting the visibility of the progressbar to invisible
                // or you can use View.GONE instead of invisible
                // View.GONE will remove the progressbar
                progressBar.visibility = View.INVISIBLE
            }).start()
        }
        save.setOnClickListener() {
            // get the bitmap of the view using
            // getScreenShotFromView method it is
            // implemented below
            val bitmap = getScreenShotFromView(mPhotograp)
            // if bitmap is not null then
            // save it to gallery
            var path = ""
            if (bitmap != null) {
                path = saveMediaToStorage(bitmap)
            }
            setNotificationChannelIntent(id,imagePath = path)
            finish()
        }
        mRecyclerList = findViewById(R.id.recyclerList)
        mHeros = mutableListOf()
        mHeroAdapter = HeroAdapter(this, object : HeroAdapter.OnItemClickListener {
            override fun onItemClick(item: Hero?) {
                val uriItem: Uri = Uri.parse(item?.imagePath)
                mPhotograp.setImageURI(uriItem)
            }

            override fun onOpenFolderClick() {
                startGalleryForResult()
            }
        })
        mRecyclerList.adapter = mHeroAdapter
        mRecyclerList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val uriGallery: Uri = Uri.parse(data?.data.toString())
                    mPhotograp.setImageURI(uriGallery)
                }
            }
        listAllImage()
        mHeroAdapter.setList(mHeros)
        createNotificationChannel(id)
    }

    private fun setNotificationChannelIntent(id: String, imagePath: String) {
        val intent = Intent(this, ImageEditorActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("FILE_PATH", imagePath)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val builder = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Save file")
            .setContentText("Finish!!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Finish")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(id: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun getLocalBitmapUri(imageView: ImageView): Uri? {
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
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file.absoluteFile, false)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName.toString() + ".provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    /**
     *
     */
    public class Handler {
        /**
         *
         */
        @Deprecated("", ReplaceWith("throw RuntimeException(\"Stub!\")"))
        fun Handler() {
            throw RuntimeException("Stub!")
        }

        /**
         *
         */
        @Deprecated("", ReplaceWith("throw RuntimeException(\"Stub!\")"))
        fun Handler(callback: android.os.Handler.Callback?) {
            throw RuntimeException("Stub!")
        }


        /**
         *
         */
        fun post(runnable: Runnable) {
        }
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
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
    private fun saveMediaToStorage(bitmap: Bitmap): String {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"
        var imagePath = ""
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
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
                imagePath= imageUri.toString()
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            imagePath=image.absolutePath
            fos = FileOutputStream(image.absoluteFile)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
        }
        return imagePath
    }

    /**
     *
     */
    fun startGalleryForResult() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun listAllImage() {
        mHeros.add(Hero("", viewType = HeroAdapter.VIEW_TYPE_TWO))
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
            mHeros.add(
                Hero(
                    name = "",
                    imagePath = cursor?.getString(dataColumnIndex).orEmpty(),
                    viewType = HeroAdapter.VIEW_TYPE_ONE
                )
            )
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursor?.close()
    }
}