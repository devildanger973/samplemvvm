package com.example.myapplication

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theartofdev.edmodo.cropper.CropImageView
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import crop.CropFragment
import crop.ImageViewTouch
import crop.OnLoadingDialogListener
import crop.OnMainBitmapChangeListener
import crop.rotate.RotateImageView
import filter.FilterListFragment
import implement.swipe.views.CollectionFragment
import paint.CustomPaintView
import paint.PaintFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


/**
 *
 */
class ImageEditorActivity : AppCompatActivity(), OnLoadingDialogListener {
    private lateinit var mHeros: MutableList<Hero>
    private lateinit var mRecyclerList: RecyclerView
    private lateinit var mHeroEditorAdapter: HeroEditorAdapter
    private lateinit var mRecyclerSelected: RecyclerView
    private lateinit var mItemSelectedAdapter: ItemSelectedAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val id: String = "my_channel_01"
    private var isMultiple: Boolean = false
    private lateinit var listHeroSelected: MutableList<HeroSelected>
    var share: ImageButton? = null
    private lateinit var progressBar: ProgressBar
    val MODE_NONE = 0
    val MODE_STICKERS = 1
    val MODE_FILTER = 2
    val MODE_CROP = 3
    val MODE_ROTATE = 4
    val MODE_TEXT = 5
    val MODE_PAINT = 6
    val MODE_BEAUTY = 7
    val MODE_BRIGHTNESS = 8
    val MODE_SATURATION = 9
    var mode: Int = MODE_NONE
    private val onMainBitmapChangeListener: OnMainBitmapChangeListener? = null
    protected var isBeenSaved = false
    protected var numberOfOperations = 0
    var save: Button? = null

    /**
     *
     */
    var mPhotograph: ImageViewTouch? = null

    /**
     *
     */
    var filePath: String? = null

    /**
     *
     */
    var list: ArrayList<String>? = null

    /**
     *
     */
//crop
    private var cropPanelEdited: CropImageView? = null

    /**
     *
     */

    /**
     *
     *///rotate
    var rotatePanelEdited: RotateImageView? = null

    /**
     *
     *///filter
    var filterListFragment: FilterListFragment? = null
    private var bitMap: Bitmap? = null
    fun getMainBit(): Bitmap? {
        return bitMap
    }

    //paint
    var paintView: CustomPaintView? = null

    public fun getCrop(): CropImageView? {
        return cropPanelEdited
    }

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        filePath = intent.getStringExtra("FILE_PATH")
        mPhotograph = findViewById(R.id.image_view)
        listHeroSelected = mutableListOf()
        list = intent.getStringArrayListExtra("LIST")

        if (filePath != null) {
            bitMap = BitmapFactory.decodeFile(filePath)
            val uri: Uri = Uri.parse(filePath)
            mPhotograph?.setImageURI(uri)
            isMultiple = false
        } else if (list != null) {
            isMultiple = true
            for (item in list ?: return) {
                listHeroSelected.add(HeroSelected(imagePath = item, viewType = 1))
            }
            if (listHeroSelected.size != 0) {
                val fileList = listHeroSelected.first().imagePath
                val uri1: Uri = Uri.parse(fileList)
                mPhotograph?.setImageURI(uri1)
                listHeroSelected.add(
                    HeroSelected(
                        "",
                        viewType = ItemSelectedAdapter.VIEW_TYPE_TWO2
                    )
                )
                Log.d("QQQQQ", "$fileList")
            } else {
                Log.d("QQQQQ", "")
            }
            bitMap = BitmapFactory.decodeFile((list ?: return).first())
        }
        Log.d("mHeroSelectedaaaaaaaaaaaa", "$list")
        mRecyclerSelected = findViewById(R.id.recyclerListSelected)
        mItemSelectedAdapter =
            ItemSelectedAdapter(this, object : ItemSelectedAdapter.OnItemClickListener {
                override fun onItemClick(item: HeroSelected) {
                    bitMap = BitmapFactory.decodeFile(item.imagePath)
                    cropPanelEdited?.setImageBitmap(bitMap)

                }

                override fun onOpenFolderClick() {

                    startAddItem(list)
                }

            })
        mRecyclerSelected.adapter = mItemSelectedAdapter
        mRecyclerSelected.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mItemSelectedAdapter.setList(listHeroSelected)
        val hide: Button = findViewById(R.id.hideListSelected)
        var i = 1
        hide.setOnClickListener {
            if (i == 1) {
                mRecyclerSelected.visibility = View.GONE
                i = 2
            } else {
                mRecyclerSelected.visibility = View.VISIBLE
                i = 1
            }
        }
        save = findViewById(R.id.save)
        save?.setOnClickListener {
            saveImage(mPhotograph)
        }
        val close: ImageButton = findViewById(R.id.close)
        close.setOnClickListener {
            finish()
            listHeroSelected.clear()
        }
        mRecyclerList = findViewById(R.id.recyclerList)
        mRecyclerList.visibility = View.GONE
        mHeros = mutableListOf()
        mHeroEditorAdapter =
            HeroEditorAdapter(this, object : HeroEditorAdapter.OnItemClickListener {
                override fun onItemClick(item: Hero?) {
                    bitMap = BitmapFactory.decodeFile(item?.imagePath)
                    cropPanelEdited?.setImageBitmap(bitMap)

                }

                override fun onOpenFolderClick() {
                    startMarsPhoto()
                    //startGalleryForResult()
                }
            })
        mRecyclerList.adapter = mHeroEditorAdapter
        mRecyclerList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    bitMap = BitmapFactory.decodeFile(data?.data.toString())
                    cropPanelEdited?.setImageBitmap(bitMap)

                }
            }
        listAllImage()
        mHeroEditorAdapter.setList(mHeros)
        createNotificationChannel(id)
        val indicatorSeekBar: IndicatorSeekBar = findViewById(R.id.seekBar)
        indicatorSeekBar.visibility = View.GONE
        indicatorSeekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                val diff: Int = seekParams.progress - previousProcess
                scaleImage(cropPanelEdited ?: return, diff)
                previousProcess = seekParams.progress
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
        }

//Tablayout Viewpager 2

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<CollectionFragment>(R.id.root)

        }
//Share

        share = findViewById(R.id.share)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        share?.setOnClickListener {
            share()
        }
//crop
        cropPanelEdited = findViewById(R.id.crop_panel)
//rotate
        rotatePanelEdited = findViewById(R.id.rotate_panel)
        filterListFragment = FilterListFragment.newInstance()
//paint
        paintView = findViewById(R.id.custom_paint_view)
    }

    fun changeMainBitmap(newBit: Bitmap?, needPushUndoStack: Boolean) {
        if (newBit == null) return
        if (bitMap == null || bitMap != newBit) {
            if (needPushUndoStack) {
                //redoUndoController.switchMainBit(bitMap, newBit)
                //increaseOpTimes()
            }
            bitMap = newBit
            mPhotograph?.setImageBitmap(bitMap)
            mPhotograph?.displayType
            if (mode == MODE_TEXT) {
                onMainBitmapChangeListener?.onMainBitmapChange()
            }
        }
    }

    //share
    private fun share() {
        progressBar.visibility = View.VISIBLE
        var s = progressBar.progress
        Thread(Runnable {
            // this loop will run until the value of i becomes 99
            while (s < 100) {
                s += 1
                // Update the progress bar and display the current value
                val handler = Handler()
                handler.post()
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            val bmpUri = getLocalBitmapUri(mPhotograph ?: return@Runnable)
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

    private fun saveImage(v: ImageViewTouch?) {
        val replyIntent = Intent(this, MainActivity::class.java)
        // get the bitmap of the view using
        // getScreenShotFromView method it is
        // implemented below
        val bitmap = getScreenShotFromView(v)
        // if bitmap is not null then
        // save it to gallery
        var path = ""
        if (bitmap != null) {
            path = saveMediaToStorage(bitmap)
            replyIntent.putExtra("EXTRA_REPLY", filePath)
            setResult(Activity.RESULT_OK, replyIntent)
            Log.d("55555", "$filePath")

        }
        setNotificationChannelIntent(id, imagePath = path)
        finish()
    }

    /**
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        listHeroSelected.clear()
    }

    /**
     *
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val list1 = intent?.getStringArrayListExtra("LIST_ITEM")
        var insertPos = listHeroSelected.size - 2
        if (list1 != null) {
            for (item1 in list1) {

                listHeroSelected.add(insertPos++, HeroSelected(imagePath = item1, viewType = 1))

            }
            mItemSelectedAdapter.setList(listHeroSelected)
        }

    }


    private fun startAddItem(imagePath: java.util.ArrayList<String>?) {
        val myIntent = Intent(this, AddItemActivity::class.java)
        myIntent.putStringArrayListExtra(
            "LIST_SELECTED",
            imagePath as kotlin.collections.ArrayList<String?>
        )
        myIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        this.startActivity(myIntent)
    }


    private fun setNotificationChannelIntent(id: String, imagePath: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("FILE_PATH", imagePath)
            putExtra("START_FROM_NOTI", true)
            Log.d("check22222", " android > Q $imagePath")

        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

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

    private val WIDTH_SCALE_RATIO = 20
    private val HEIGHT_SCALE_RATIO = 20
    private var previousProcess = 0

    /**
     *
     */
    fun scaleImage(img: CropImageView, scale: Int) {
        var bitmap = (img.drawToBitmap() as BitmapDrawable).bitmap
        var width = bitmap.width.toFloat()
        var height = bitmap.height.toFloat()
        width += scale * WIDTH_SCALE_RATIO
        height += scale * HEIGHT_SCALE_RATIO
        bitmap = Bitmap.createScaledBitmap(
            bitmap, width.toInt(), height.toInt(),
            true
        )
        img.setImageBitmap(bitmap)
    }

    private fun createNotificationChannel(id: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

    private fun getLocalBitmapUri(imageView: ImageViewTouch): Uri? {
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
    class Handler {
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
        fun post() {
        }
    }

    private fun getScreenShotFromView(v: ImageViewTouch?): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            if (v != null) {
                screenshot =
                    Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            }
            // Now draw this bitmap on a canvas
            val canvas = screenshot?.let { Canvas(it) }
            v?.draw(canvas)
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
                imagePath = imageUri.toString()
                Log.d("check22222", imagePath)
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            imagePath = image.absolutePath
            fos = FileOutputStream(image.absoluteFile, false)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
        }
        return imagePath
    }

    private fun startMarsPhoto() {
        val myIntent = Intent(this, MarsPhotoActivity::class.java)
        this.startActivity(myIntent)
    }

    /**
     *
     */
//    fun startGalleryForResult() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        resultLauncher.launch(intent)
//    }

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
            val a = 1
            mHeros.add(
                Hero(
                    name = "image ${a + i}",
                    imagePath = cursor?.getString(dataColumnIndex).orEmpty(),
                    viewType = HeroAdapter.VIEW_TYPE_ONE
                )
            )
            Log.i("PATH", arrPath[i].orEmpty())
        }
// The cursor should be freed up after use with close()
        cursor?.close()
    }

    /**
     *
     */
    override fun showLoadingDialog() {
    }

    /**
     *
     */
    override fun dismissLoadingDialog() {
    }
}