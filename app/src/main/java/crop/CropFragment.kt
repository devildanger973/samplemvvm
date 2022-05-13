package crop

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
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

    val INDEX: Int = ModuleConfig.INDEX_CROP


    /**
     *
     */

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

    private lateinit var root: View

    //Crop apply
    private var disposables: CompositeDisposable? = CompositeDisposable()
    private var loadingDialogListener: OnLoadingDialogListener? = null
    private val selectedTextView: TextView? = null
    private val UNSELECTED_COLOR = R.color.kelly_2

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
        activity?.mode = (activity ?: return).MODE_NONE
        cropPanel!!.visibility = View.GONE
        activity?.mPhotograph?.visibility = View.VISIBLE
        activity?.mPhotograph?.setScaleEnabled(true)
        selectedTextView?.setTextColor(getColorFromRes(UNSELECTED_COLOR))
    }

    private fun getColorFromRes(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(requireActivity(), resId)
    }

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        root = inflater.inflate(R.layout.crop, container, false)
        mItemCrop = mutableListOf()
        mCropAdapter = CropAdapter(requireActivity(), object : CropAdapter.OnItemClickListener {
            override fun onItemClick(item: ItemCrop?) {
                startCrop(item)

            }
        })
        cropPanel = ensureEditActivity()?.getCrop()
        rotatePanel = ensureEditActivity()?.rotatePanelEdited
        addItemCrop()
        rotate()
        rotateR()
        cancel()
        apply()
        root.findViewById<RecyclerView>(R.id.recyclerCrop).adapter = mCropAdapter
        mCropAdapter.setList(mItemCrop)
        return root
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            viewImage()
            rotate()
        }
    }

    //ViewImage
    fun viewImage() {
        activity?.mode = ensureEditActivity()!!.MODE_CROP
        rotatePanel?.visibility = View.GONE
        activity?.mPhotograph?.visibility = View.VISIBLE
        cropPanel!!.visibility = View.VISIBLE
        activity?.mPhotograph?.displayType
        activity?.mPhotograph?.setScaleEnabled(false)

        cropPanel!!.setImageBitmap(activity?.getMainBit())
        cropPanel!!.setFixedAspectRatio(false)
    }

    //Rotate
    private fun rotate() {
        val rotate: ImageView = root.findViewById(R.id.rotate)
        rotateLeft = root.findViewById(R.id.rotate_left)
        rotateRight = root.findViewById(R.id.rotate_right)
        rotate.setOnClickListener {
            viewRotate()
        }
        loadingDialogListener = ensureEditActivity()

    }

    private fun viewRotate() {
        activity?.mode = (ensureEditActivity() ?: return).MODE_ROTATE
        cropPanel?.visibility = View.GONE
        activity?.rotatePanelEdited?.setImageBitmap(activity?.getMainBit())
        activity?.mPhotograph?.visibility = View.GONE
        activity?.rotatePanelEdited?.visibility = View.VISIBLE
        //activity?.rotatePanelEdited?.reset()
        activity?.mPhotograph?.displayType

    }

    private fun rotateR() {
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
        if (rotatePanel?.rotateAngle === 0f || (rotatePanel ?: return).rotateAngle % 360 === 0f) {
            backToMain()
        } else {
            applyRotationDisposable = (applyRotation(activity?.getMainBit()!!)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe { loadingDialogListener?.showLoadingDialog() }
                ?.doFinally { loadingDialogListener?.dismissLoadingDialog() }
                ?.subscribe({ processedBitmap: Bitmap? ->
                    if (processedBitmap == null) return@subscribe
                    activity?.changeMainBitmap(processedBitmap, true)
                    backToMain()

                }, { _: Throwable? -> })
                ?: disposables?.add(applyRotationDisposable)) as Disposable
            Log.d("nhan", "disposables$disposables ")
            Log.d("nhan", " applyRotationDisposable$applyRotationDisposable")


        }
    }

    private fun applyRotation(sourceBitmap: Bitmap): Single<Bitmap>? {
        return Single.fromCallable {
            val imageRect: RectF = rotatePanel?.getImageNewRect(activity?.getMainBit())!!
            val resultBitmap = Bitmap.createBitmap(imageRect.width().toInt(),
                imageRect.height().toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)
            val w = sourceBitmap.width shr 1
            val h = sourceBitmap.height shr 1
            val centerX = imageRect.width() / 2
            val centerY = imageRect.height() / 2
            val left = centerX - w
            val top = centerY - h
            val destinationRect = RectF(left, top, left + sourceBitmap.width, top
                    + sourceBitmap.height)
            canvas.save()
            canvas.rotate(
                activity?.rotatePanelEdited!!.rotateAngle,
                imageRect.width() / 2,
                imageRect.height() / 2
            )
            canvas.drawBitmap(
                sourceBitmap,
                Rect(
                    0,
                    0,
                    sourceBitmap.width,
                    sourceBitmap.height),
                destinationRect,
                null)
            canvas.restore()


            resultBitmap
        }
    }

    //Rotate
    private fun cancel() {
        val cancelBtn: ImageView = root.findViewById(R.id.cancel_crop)
        cancelBtn.setOnClickListener {
            rotatePanel?.visibility = View.GONE
            cropPanel?.visibility = View.GONE
            activity?.mPhotograph?.visibility = View.VISIBLE
        }
    }

    // crop apply
    private fun apply() {
        val applyBtn: ImageView = root.findViewById(R.id.apply)
        applyBtn.setOnClickListener {
            activity?.mPhotograph?.visibility = View.VISIBLE
            cropPanel?.visibility = View.GONE
            rotatePanel?.visibility = View.GONE
            /*when (activity?.mode) {
                activity?.MODE_CROP -> applyCropImage()
                activity?.MODE_ROTATE -> applyRotateImage()
                else -> {}
            }*/
            if (activity?.mode == activity?.MODE_CROP) {
                applyCropImage()
            } else if (activity?.mode == activity?.MODE_ROTATE) {
                applyRotateImage()
            }
        }
        loadingDialogListener = ensureEditActivity()

    }

    private fun applyCropImage() {
        disposables?.add(
            getCroppedBitmap()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingDialogListener?.showLoadingDialog() }
                .doFinally { loadingDialogListener?.dismissLoadingDialog() }
                .subscribe({ bitmap: Bitmap? ->
                    activity?.changeMainBitmap(bitmap, true)
                    backToMain()
                }, { e: Throwable ->
                    e.printStackTrace()
                    backToMain()
                    Toast.makeText(context, "Error while saving image", Toast.LENGTH_SHORT).show()
                })
        )
    }

    private fun getCroppedBitmap(): Single<Bitmap> {
        return Single.fromCallable { cropPanel!!.croppedImage }
    }

    //crop apply


    private fun startCrop(item: ItemCrop?) {
        activity?.mode = ensureEditActivity()!!.MODE_CROP
        cropPanel!!.setImageBitmap(activity?.getMainBit())
        activity?.mPhotograph?.visibility = View.VISIBLE
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

    override fun onStop() {
        disposables!!.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables!!.dispose()
        super.onDestroy()
    }

}