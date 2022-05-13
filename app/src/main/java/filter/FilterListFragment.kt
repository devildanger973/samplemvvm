package filter

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import crop.BaseEditFragment
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
        activity?.mode = ensureEditActivity()!!.MODE_NONE
        currentBitmap = activity?.getMainBit()
        filterBitmap = null
        ensureEditActivity()?.mPhotograph?.setImageBitmap(ensureEditActivity()?.getMainBit())
        ensureEditActivity()?.mPhotograph?.setScaleEnabled(true)
    }

    private lateinit var mFilterAdapter: FilterAdapter

    private lateinit var root: View
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
        savedInstanceState: Bundle?,
    ): View {
        root = inflater.inflate(R.layout.filter, container, false)
        val filterRecyclerView: RecyclerView = root.findViewById(R.id.filter_recycler)
        val btnApply: ImageView = root.findViewById(R.id.apply1)
        mFilterAdapter = FilterAdapter(this, requireContext())
        filterRecyclerView.adapter = mFilterAdapter
        val backBtn: View = root.findViewById(R.id.back_to_main)
        backBtn.setOnClickListener { backToMain() }
        btnApply.setOnClickListener { applyFilterImage() }
        return root
    }

    fun applyFilterImage() {
        if (currentBitmap == activity?.getMainBit()) {
            backToMain()
        } else {
            activity?.changeMainBitmap(filterBitmap, true)
            backToMain()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            viewImage()
        }
    }

    /**
     *
     */
    fun enableFilter(filterIndex: Int) {
        if (filterIndex == NULL_FILTER_INDEX) {
            ensureEditActivity()?.mPhotograph?.setImageBitmap(ensureEditActivity()?.getMainBit())
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
        ensureEditActivity()?.mPhotograph?.setImageBitmap(filterBitmap)
        currentBitmap = filterBitmap
    }

    private fun applyFilter(filterIndex: Int): Single<Bitmap?> {
        return Single.fromCallable {
            val srcBitmap: Bitmap = Bitmap.createBitmap(
                activity?.getMainBit()!!.copy(
                    Bitmap.Config.RGB_565, true
                )
            )
            PhotoProcessing.filterPhoto(srcBitmap, filterIndex)
        }
    }

    override fun onShow() {
        /*activity?.mode = ensureEditActivity()!!.MODE_FILTER
        ensureEditActivity()?.mPhotograph?.visibility = View.VISIBLE
        ensureEditActivity()?.filterListFragment?.setCurrentBitmap(ensureEditActivity()?.getMainBit())
        if (ensureEditActivity()?.filePath != null) {
            bitMap = BitmapFactory.decodeFile(ensureEditActivity()?.filePath)
            ensureEditActivity()?.mPhotograph?.setImageBitmap(bitMap)
            ensureEditActivity()?.mPhotograph?.displayType
        } else if (ensureEditActivity()?.list != null) {
            for (item in ensureEditActivity()?.list ?: return) {
                val fileList = (ensureEditActivity()?.list ?: return).first()
                bitMap = BitmapFactory.decodeFile(fileList)
                ensureEditActivity()?.mPhotograph?.setImageBitmap(ensureEditActivity()?.getMainBit())
                ensureEditActivity()?.mPhotograph?.displayType
            }
        }
        ensureEditActivity()?.mPhotograph?.setScaleEnabled(false)*/
    }

    fun viewImage() {
        activity?.mPhotograph?.visibility = View.VISIBLE
        activity?.filterListFragment?.setCurrentBitmap(activity?.getMainBit())
        if (activity?.filePath != null) {
            activity?.mPhotograph?.setImageBitmap(activity?.getMainBit())
            activity?.mPhotograph?.displayType
        } else if (activity?.list != null) {
            for (item in activity?.list ?: return) {
                activity?.mPhotograph?.setImageBitmap(activity?.getMainBit())
                activity?.mPhotograph?.displayType
            }
        }
        activity?.mPhotograph?.setScaleEnabled(false)

    }

}