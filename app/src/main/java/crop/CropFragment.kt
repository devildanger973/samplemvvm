package crop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    var mPhotograph1: ImageView? = null
    private var list1: ArrayList<String>? = null
    private var bitMap: Bitmap? = null
    private lateinit var root: View

    //Crop apply
    private var disposables: CompositeDisposable? = CompositeDisposable()
    private var loadingDialogListener: OnLoadingDialogListener? = null
    val MODE_NONE = 0
    val MODE_TEXT = 5
    var mode: Int = MODE_NONE
    private val onMainBitmapChangeListener: OnMainBitmapChangeListener? = null
    private var numberOfOperations = 0
    private var isBeenSaved = false


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
        mPhotograph1 = ensureEditActivity()?.mPhotograph
        addItemCrop()
        viewImage()
        apply()
        root.findViewById<RecyclerView>(R.id.recyclerCrop).adapter = mCropAdapter
        mCropAdapter.setList(mItemCrop)
        return root

    }

    // crop apply
    private fun apply() {
        val applyBtn: ImageView = root.findViewById(R.id.apply)
        applyBtn.setOnClickListener() {
            applyCropImage()
            mPhotograph1?.visibility = View.VISIBLE
            cropPanel?.visibility = View.GONE
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
                    changeMainBitmap(bitmap, true)
                    backToMain()
                }, { e: Throwable ->
                    e.printStackTrace()
                    backToMain()
                    Toast.makeText(context, "Error while saving image", Toast.LENGTH_SHORT).show()
                })
        )
    }

    private fun changeMainBitmap(newBit: Bitmap?, needPushUndoStack: Boolean) {
        if (newBit == null) return
        if (bitMap == null || bitMap != newBit) {
            if (needPushUndoStack) {
                //redoUndoController.switchMainBit(bitMap, newBit)
                increaseOpTimes()
            }
            bitMap = newBit
            mPhotograph1?.setImageBitmap(bitMap)
            cropPanel?.setImageBitmap(bitMap)
            //mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN)
            if (mode == MODE_TEXT) {
                onMainBitmapChangeListener?.onMainBitmapChange()
            }
        }
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
        ensureEditActivity()?.cropPanelEdited?.visibility = View.VISIBLE
        if (ensureEditActivity()?.filePath != null) {
            filePath1 = ensureEditActivity()?.filePath
            bitMap = BitmapFactory.decodeFile(filePath1)
            cropPanel?.setImageBitmap(bitMap)
        } else if (ensureEditActivity()?.list != null) {
            list1 = ensureEditActivity()?.list
            for (item in list1 ?: return) {
                val fileList = (list1 ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                cropPanel?.setImageBitmap(bitMap)
            }
        }
    }

    private fun startCrop(item: ItemCrop?) {
        mPhotograph1?.visibility = View.GONE
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
                ratioText = RatioText.RATIO_4_5, itemCrop = R.drawable.hulk,
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
                ratioText = RatioText.RATIO_3_4, itemCrop = R.drawable.hulk,
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
                ratioText = RatioText.RATIO_1_2, itemCrop = R.drawable.hulk,
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
                ratioText = RatioText.RATIO_2_3, itemCrop = R.drawable.hulk,
                name = "2:3"
            )
        )
        mItemCrop.add(
            ItemCrop(
                ratioText = RatioText.RATIO_2_1, itemCrop = R.drawable.hulk,
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