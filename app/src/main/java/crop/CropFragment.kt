package crop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.theartofdev.edmodo.cropper.CropImageView
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
    private var list1: ArrayList<String>? = null

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
        val root = inflater.inflate(R.layout.crop, container, false)
        mItemCrop = mutableListOf()
        mCropAdapter = CropAdapter(requireActivity(), object : CropAdapter.OnItemClickListener {
            override fun onItemClick(item: ItemCrop?) {
                startCrop(item)
            }
        })
        addItemCrop()
        viewImage()
        root.findViewById<RecyclerView>(R.id.recyclerCrop).adapter = mCropAdapter
        mCropAdapter.setList(mItemCrop)
        return root

    }

    private fun viewImage() {
        var bitMap: Bitmap
        ensureEditActivity()?.cropPanelEdited?.visibility = View.VISIBLE
        if (ensureEditActivity()?.filePath != null) {
            filePath1 = ensureEditActivity()?.filePath
            bitMap = BitmapFactory.decodeFile(filePath1)
            ensureEditActivity()?.cropPanelEdited?.setImageBitmap(bitMap)
            cropPanel = ensureEditActivity()?.cropPanelEdited
        } else if (ensureEditActivity()?.list != null) {
            list1 = ensureEditActivity()?.list
            for (item in list1 ?: return) {
                val fileList = (list1 ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                ensureEditActivity()?.cropPanelEdited?.setImageBitmap(bitMap)
                cropPanel = ensureEditActivity()?.cropPanelEdited
            }
        }
    }

    private fun startCrop(item: ItemCrop?) {
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