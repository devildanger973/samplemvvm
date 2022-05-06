package filter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import crop.BaseEditFragment
import crop.ImageViewTouch
import crop.OnLoadingDialogListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 *
 */
class FilterListFragment : BaseEditFragment() {


    companion object {
        fun newInstance(): FilterListFragment? {
            return FilterListFragment()
        }
    }

    override fun backToMain() {
        currentBitmap = ensureEditActivity()?.getMainBit()
        filterBitmap = null
        mPhotograph1?.setImageBitmap(ensureEditActivity()?.getMainBit())
        mPhotograph1?.setScaleEnabled(true)
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
    private var filterBitmap: Bitmap? = null
    private val compositeDisposable = CompositeDisposable()

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
        //viewImage()
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filterRecyclerView.layoutManager = layoutManager
        filterRecyclerView.adapter = mFilterAdapter
        val backBtn: View = root.findViewById(R.id.back_to_main)
        backBtn.setOnClickListener { backToMain() }
        return root
    }

    /**
     *
     */
    fun enableFilter(filterIndex: Int) {
        if (filterIndex == NULL_FILTER_INDEX) {
            mPhotograph1?.setImageBitmap(ensureEditActivity()?.getMainBit())
            currentBitmap = ensureEditActivity()?.getMainBit()
            return
        }
        val applyFilterDisposable: Disposable = applyFilter(filterIndex)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loadingDialogListener?.showLoadingDialog() }
            .doFinally { loadingDialogListener?.dismissLoadingDialog() }
            .subscribe(
                { bitmapWithFilter: Bitmap? ->
                    this.updatePreviewWithFilter(
                        bitmapWithFilter
                    )
                },
                { showSaveErrorToast() }
            )
        compositeDisposable.add(applyFilterDisposable)
        /*Handler().post{
            applyFilter(filterIndex)
        }*/
    }

    private fun setCurrentBitmap(currentBitmap: Bitmap?) {
        this.currentBitmap = currentBitmap
    }

    private fun showSaveErrorToast() {
        Toast.makeText(
            getActivity(),
            "Error",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updatePreviewWithFilter(bitmapWithFilter: Bitmap?) {
        if (bitmapWithFilter == null) return
        if (filterBitmap != null && !(filterBitmap?.isRecycled ?: return)) {
            filterBitmap?.recycle()
        }
        filterBitmap = bitmapWithFilter
        mPhotograph1?.setImageBitmap(filterBitmap)
        currentBitmap = filterBitmap
    }

    private fun applyFilter(filterIndex: Int): Single<Bitmap?> {
        /*val srcBitmap: Bitmap = Bitmap.createBitmap(
            ensureEditActivity()?.getMainBit()!!.copy(
                Bitmap.Config.RGB_565, true
            )
        )
        PhotoProcessing.filterPhoto(srcBitmap, filterIndex)*/
        return Single.fromCallable {
            val srcBitmap: Bitmap = Bitmap.createBitmap(
                ensureEditActivity()?.getMainBit()!!.copy(
                    Bitmap.Config.RGB_565, true
                )
            )
            PhotoProcessing.filterPhoto(srcBitmap, filterIndex)
        }
    }

    override fun onShow() {
        ensureEditActivity()?.filterListFragment?.setCurrentBitmap(ensureEditActivity()?.getMainBit())
        if (ensureEditActivity()?.filePath != null) {
            mPhotograph1?.setImageBitmap(ensureEditActivity()?.getMainBit())
            mPhotograph1?.displayType
            mPhotograph1?.visibility = View.VISIBLE

        } else if (ensureEditActivity()?.list != null) {
            list1 = ensureEditActivity()?.list
            for (item in list1 ?: return) {
                val fileList = (list1 ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                mPhotograph1?.setImageBitmap(bitMap)
                mPhotograph1?.displayType
                mPhotograph1?.visibility = View.VISIBLE

            }
        }
        mPhotograph1?.setScaleEnabled(false)
    }

    private fun viewImage() {
        mPhotograph1?.visibility = View.VISIBLE
        ensureEditActivity()?.filterListFragment?.setCurrentBitmap(ensureEditActivity()?.getMainBit())
        /*if (ensureEditActivity()?.filePath != null) {
            filePath1 = ensureEditActivity()?.filePath
            bitMap = BitmapFactory.decodeFile(filePath1)
            mPhotograph1?.setImageBitmap(bitMap)
            mPhotograph1?.displayType
        } else if (ensureEditActivity()?.list != null) {
            list1 = ensureEditActivity()?.list
            for (item in list1 ?: return) {
                val fileList = (list1 ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                mPhotograph1?.setImageBitmap(bitMap)
                mPhotograph1?.displayType
            }
        }
        mPhotograph1?.setScaleEnabled(false)*/

    }

}