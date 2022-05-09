package crop

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.theartofdev.edmodo.cropper.CropImageView
import crop.rotate.RotateImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.roundToInt


/**
 *
 */
class CropFragment : BaseEditFragment() {
    private lateinit var mItemCrop: MutableList<ItemCrop>
    private lateinit var mCropAdapter: CropAdapter


    /**
     *
     */
    private var filePath1: String? = null

    /**
     *
     */
    private var cropPanel: CropImageView? = null

    /**
     *
     */
    /**
     *
     */

    private var mPhotograph1: ImageViewTouch? = null
    private var list1: ArrayList<String>? = null
    private var bitMap: Bitmap? = null
    private lateinit var root: View

    //Crop apply
    private var disposables: CompositeDisposable? = CompositeDisposable()
    private var loadingDialogListener: OnLoadingDialogListener? = null
    private val MODE_NONE = 0
    private val MODE_TEXT = 5
    private var mode: Int = MODE_NONE
    private val onMainBitmapChangeListener: OnMainBitmapChangeListener? = null
    private var numberOfOperations = 0
    private var isBeenSaved = false

    //rotate
    private var rotatePanel: RotateImageView? = null
    private var rotateLeft: ImageView? = null
    private var rotateRight: ImageView? = null
    private lateinit var applyRotationDisposable: Disposable

    /**
     *
     */
    override fun onShow() {
    }

    override fun backToMain() {
    }

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.crop, container, false)
        mItemCrop = mutableListOf()
        mCropAdapter = CropAdapter(requireActivity(), object : CropAdapter.OnItemClickListener {
            override fun onItemClick(item: ItemCrop?) {
                startCrop(item)

            }
        })
        cropPanel = ensureEditActivity()?.cropPanelEdited
        rotatePanel = ensureEditActivity()?.rotatePanelEdited
        mPhotograph1 = ensureEditActivity()?.mPhotograph
        addItemCrop()
        viewImage()
        rotate()
        cancel()
        apply()
        root.findViewById<RecyclerView>(R.id.recyclerCrop).adapter = mCropAdapter
        mCropAdapter.setList(mItemCrop)
        return root
    }

    //Rotate
    private fun rotate() {
        val rotate: ImageView = root.findViewById(R.id.rotate)
        rotateLeft = root.findViewById(R.id.rotate_left)
        rotateRight = root.findViewById(R.id.rotate_right)
        mPhotograph1?.displayType
        rotatePanel?.setImageBitmap(getMainBit())
        rotatePanel?.reset()
        rotate.setOnClickListener {
            startRotate()
            Log.d("getMainBit()()", "$bitMap")
        }
        loadingDialogListener = ensureEditActivity()

    }

    private fun startRotate() {
        cropPanel?.visibility = View.GONE
        mPhotograph1?.visibility = View.GONE
        rotatePanel?.visibility = View.VISIBLE
        rotateLeft?.setOnClickListener {
            rotatePanel?.rotation = ((rotatePanel?.rotateAngle ?: return@setOnClickListener) - 90)
            rotatePanel?.rotateImage(rotatePanel?.rotation)
        }
        rotateRight?.setOnClickListener {
            rotatePanel?.rotation = ((rotatePanel?.rotateAngle ?: return@setOnClickListener) + 90)
            rotatePanel?.rotateImage(rotatePanel?.rotation)


        }
    }

    private fun applyRotateImage() {
        if (rotatePanel!!.rotateAngle === 0f || rotatePanel!!.rotateAngle % 360 === 0f) {
            backToMain()
        } else {
            applyRotationDisposable = (applyRotation(getMainBit() ?: return)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe { loadingDialogListener?.showLoadingDialog() }
                ?.doFinally { loadingDialogListener?.dismissLoadingDialog() }
                ?.subscribe({ processedBitmap: Bitmap? ->
                    if (processedBitmap == null) return@subscribe
                    applyAndExit(processedBitmap)
                }, { _: Throwable? -> })
                ?: disposables?.add(applyRotationDisposable)) as Disposable
        }
    }

    private fun applyRotation(sourceBitmap: Bitmap): Single<Bitmap>? {
        return Single.fromCallable {
            val imageRect: RectF = rotatePanel?.imageNewRect!!
            val resultBitmap = Bitmap.createBitmap(
                imageRect.width().toInt(),
                imageRect.height().toInt(), Bitmap.Config.ARGB_4444
            )
            val canvas = Canvas(resultBitmap)
            val w = sourceBitmap.width shr 1
            val h = sourceBitmap.height shr 1
            val centerX = imageRect.width() / 2
            val centerY = imageRect.height() / 2
            val left = centerX - w
            val top = centerY - h
            val destinationRect = RectF(
                left, top, left + sourceBitmap.width, top
                        + sourceBitmap.height
            )
            canvas.save()
            canvas.rotate(
                rotatePanel!!.rotateAngle,
                imageRect.width() / 2,
                imageRect.height() / 2
            )
            canvas.drawBitmap(
                sourceBitmap,
                Rect(
                    0,
                    0,
                    sourceBitmap.width,
                    sourceBitmap.height
                ),
                destinationRect,
                null
            )
            canvas.restore()
            resultBitmap
        }
    }

    private fun applyAndExit(resultBitmap: Bitmap) {
        ensureEditActivity()?.changeMainBitmap(resultBitmap, true)
        backToMain()
    }

    //Rotate
    private fun cancel() {
        val cancelBtn: ImageView = root.findViewById(R.id.cancel_crop)
        cancelBtn.setOnClickListener {
            rotatePanel?.visibility = View.GONE
            cropPanel?.visibility = View.GONE
            mPhotograph1?.visibility = View.VISIBLE
        }
    }

    // crop apply
    private fun apply() {
        val applyBtn: ImageView = root.findViewById(R.id.apply)
        applyBtn.setOnClickListener {
            mPhotograph1?.visibility = View.VISIBLE
            cropPanel?.visibility = View.GONE
            rotatePanel?.visibility = View.GONE
            applyCropImage()
            //applyRotateImage()
            /*when (mode) {
                MODE_CROP -> applyCropImage()
                MODE_ROTATE -> applyRotateImage()
            }*/
        }
        loadingDialogListener = ensureEditActivity()
        Log.d("getMainBit()()", "${bitMap}")

    }

    private fun applyCropImage() {
        disposables?.add(
            getCroppedBitmap()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingDialogListener?.showLoadingDialog() }
                .doFinally { loadingDialogListener?.dismissLoadingDialog() }
                .subscribe({ bitmap: Bitmap? ->
                    ensureEditActivity()?.changeMainBitmap(bitmap, true)
                    backToMain()
                }, { e: Throwable ->
                    e.printStackTrace()
                    backToMain()
                    Toast.makeText(context, "Error while saving image", Toast.LENGTH_SHORT).show()
                })
        )
    }

    private fun increaseOpTimes() {
        numberOfOperations++
        isBeenSaved = false
    }

    private fun getCroppedBitmap(): Single<Bitmap> {
        return Single.fromCallable { cropPanel!!.croppedImage }
    }

    //crop apply
//ViewImage
    private fun viewImage() {
        mPhotograph1?.visibility = View.GONE
        rotatePanel?.visibility = View.GONE
        cropPanel?.visibility = View.VISIBLE
        if (ensureEditActivity()?.filePath != null) {
            filePath1 = ensureEditActivity()?.filePath
            bitMap = BitmapFactory.decodeFile(filePath1)
            cropPanel?.setImageBitmap(getMainBit())
            mPhotograph1?.displayType
            cropPanel?.setFixedAspectRatio(false)
        } else if (ensureEditActivity()?.list != null) {
            list1 = ensureEditActivity()?.list
            for (item in list1 ?: return) {
                val fileList = (list1 ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                cropPanel?.setImageBitmap(getMainBit())
                mPhotograph1?.displayType
                cropPanel?.setFixedAspectRatio(false)
            }
        }
        Log.d("getMainBit()()", "$bitMap")

    }

    private fun getMainBit(): Bitmap? {
        return bitMap
    }

    private fun startCrop(item: ItemCrop?) {
        mPhotograph1?.visibility = View.GONE
        rotatePanel?.visibility = View.GONE
        cropPanel?.visibility = View.VISIBLE
        when {
            (item ?: return).ratioText === RatioText.FREE -> {
                cropPanel?.setFixedAspectRatio(false)
            }
            /*(item ?: return).ratioText === RatioText.FIT_IMAGE -> {
                val currentBmp: Bitmap? = ensureEditActivity()?.getMainBit()
                cropPanel?.setAspectRatio((currentBmp ?: return).width, currentBmp.height)
            }*/
            else -> {
                val aspectRatio: AspectRatio = (item?.ratioText ?: return).aspectRatio
                cropPanel?.setAspectRatio(aspectRatio.aspectX.roundToInt(), aspectRatio.aspectY)
            }
        }
        Log.d("getMainBit()()", "$bitMap")

    }
//ViewImage
    /**
     *
     */
    private fun addItemCrop() {
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.FIT_IMAGE,
                itemCrop = R.drawable.ic_baseline_crop_30,
                name = "Goc"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.FREE,
                itemCrop = R.drawable.ic_baseline_crop_free_30,
                name = "Free"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.SQUARE,
                itemCrop = R.drawable.ic_baseline_crop_din_30,
                name = "1:1"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_4_5, itemCrop = R.drawable.spiderman,
                name = "4:5"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_IG_story,
                itemCrop = R.drawable.ic_baseline_crop_portrait_30,
                name = "IG"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_4_3,
                itemCrop = R.drawable.ic_baseline_crop_landscape_30,
                name = "4:3"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_3_4, itemCrop = R.drawable.kin,
                name = "3:4"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_3_2,
                itemCrop = R.drawable.ic_baseline_crop_3_2_24,
                name = "3:2"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_9_16, itemCrop = R.drawable.hulk,
                name = "9:16"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_16_9,
                itemCrop = R.drawable.ic_baseline_crop_16_9_30,
                name = "16:9"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_1_2, itemCrop = R.drawable.kin,
                name = "1:2"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_Cover, itemCrop = R.drawable.hulk,
                name = "Cover"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_2_3, itemCrop = R.drawable.kin,
                name = "2:3"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_2_1,
                itemCrop = R.drawable.ic_baseline_crop_landscape_30,
                name = "2:1"
            )
        )
    }

    /**
     *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}