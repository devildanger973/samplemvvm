package filter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import crop.BaseEditFragment
import crop.ImageViewTouch
import crop.OnLoadingDialogListener

/**
 *
 */
class FilterListFragment : BaseEditFragment() {
    override fun onShow() {
    }

    override fun backToMain() {
    }

    private lateinit var mFilterAdapter: FilterAdapter

    private var filePath1: String? = null
    private var bitMap: Bitmap? = null
    private lateinit var root: View
    private var mPhotograph1: ImageViewTouch? = null
    private var list1: ArrayList<String>? = null
    val NULL_FILTER_INDEX = 0
    private var currentBitmap: Bitmap? = null
    private var loadingDialogListener: OnLoadingDialogListener? = null

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.filter, container, false)
        val filterRecyclerView: RecyclerView = root.findViewById(R.id.filter_recycler)

        mFilterAdapter = FilterAdapter(this, requireContext())
        mPhotograph1 = ensureEditActivity()?.mPhotograph
        filePath1 = ensureEditActivity()?.filePath
        viewImage()
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filterRecyclerView.layoutManager = layoutManager
        filterRecyclerView.adapter = mFilterAdapter
        return root
    }

    private fun getMainBit(): Bitmap? {
        return bitMap
    }

    /**
     *
     */
    fun enableFilter(filterIndex: Int) {
        if (filterIndex == NULL_FILTER_INDEX) {
            mPhotograph1?.setImageBitmap(getMainBit())
            currentBitmap = getMainBit()
            return
        }
        /*val applyFilterDisposable: Disposable = applyFilter(filterIndex)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { loadingDialogListener?.showLoadingDialog() }
            ?.doFinally { loadingDialogListener?.dismissLoadingDialog() }
            .subscribe(
                Consumer { bitmapWithFilter: Bitmap? ->
                    this.updatePreviewWithFilter(
                        bitmapWithFilter
                    )
                },
                Consumer { e: Throwable? -> showSaveErrorToast() }
            )
        compositeDisposable.add(applyFilterDisposable)*/
    }

    private fun setCurrentBitmap(currentBitmap: Bitmap?) {
        this.currentBitmap = currentBitmap
    }

    private fun viewImage() {
        mPhotograph1?.visibility = View.VISIBLE
        ensureEditActivity()?.filterListFragment?.setCurrentBitmap(getMainBit())
        if (ensureEditActivity()?.filePath != null) {
            filePath1 = ensureEditActivity()?.filePath
            bitMap = BitmapFactory.decodeFile(filePath1)
            //cropPanel?.setImageBitmap(bitMap)
            mPhotograph1?.displayType
            //cropPanel?.setFixedAspectRatio(false)
        } else if (ensureEditActivity()?.list != null) {
            list1 = ensureEditActivity()?.list
            for (item in list1 ?: return) {
                val fileList = (list1 ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                //cropPanel?.setImageBitmap(bitMap)
                mPhotograph1?.displayType
                //cropPanel?.setFixedAspectRatio(false)
            }
        }
        Log.d("getMainBit()()", "$bitMap")

    }

}