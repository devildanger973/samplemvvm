package sticker

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ImageEditorActivity
import com.example.myapplication.R
import crop.BaseEditFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import paint.BaseActivity
import paint.Matrix3

/**
 *
 */
class StickerFragment : BaseEditFragment() {
    private var mainView: View? = null
    private var flipper: ViewFlipper? = null

    /**
     *
     */
    var stickerView: StickerView? = null
    private var stickerAdapter: StickerAdapter? = null
    private val compositeDisposable = CompositeDisposable()
    private var loadingDialog: Dialog? = null

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mainView = inflater.inflate(R.layout.fragment_edit_image_sticker_type,
            null)
        loadingDialog = BaseActivity.getLoadingDialog(getActivity() ?: return null,
            R.string.saving_image,
            false)
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        stickerView = (activity ?: return).stickerView
        flipper = mainView?.findViewById(R.id.flipper)
        flipper?.setInAnimation(activity, R.anim.in_bottom_to_top)
        flipper?.setOutAnimation(activity, R.anim.out_bottom_to_top)
        val typeList: RecyclerView = (mainView ?: return)
            .findViewById(R.id.stickers_type_list)
        typeList.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        typeList.layoutManager = mLayoutManager
        typeList.adapter = StickerTypeAdapter(this)
        val stickerList: RecyclerView = (mainView ?: return).findViewById(R.id.stickers_list)
        stickerList.setHasFixedSize(true)
        val stickerListLayoutManager = LinearLayoutManager(
            activity)
        stickerListLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        stickerList.layoutManager = stickerListLayoutManager
        stickerAdapter = StickerAdapter(this)
        stickerList.adapter = stickerAdapter
        val backToMenu = mainView?.findViewById<View>(R.id.back_to_main)
        backToMenu?.setOnClickListener(BackToMenuClick())
        val backToType = mainView?.findViewById<View>(R.id.back_to_type)
        val applySticker: ImageView = (mainView ?: return).findViewById(R.id.apply_sticker)
        applySticker.setOnClickListener {
            applyStickers()
            flipper?.showPrevious()
        }
        backToType?.setOnClickListener { flipper?.showPrevious() }
    }

    override fun backToMain() {
        stickerView?.clear()
        stickerView?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            view()
        }
    }

    /**
     *
     */
    fun view() {
        activity?.stickerView?.visibility = View.VISIBLE
    }

    override fun onShow() {
    }

    /**
     *
     */
    fun swipToStickerDetails(path: String?, stickerCount: Int) {
        stickerAdapter?.addStickerImages(path ?: return, stickerCount)
        flipper?.showNext()
    }

    /**
     *
     */
    fun selectedStickerItem(path: String?) {
        val imageKey = resources.getIdentifier(path, "drawable", (context ?: return).packageName)
        val bitmap = BitmapFactory.decodeResource(resources, imageKey)
        stickerView?.addBitImage(bitmap)
    }

    private inner class BackToMenuClick : View.OnClickListener {
        override fun onClick(v: View) {
            backToMain()
        }
    }

    /**
     *
     */
    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    /**
     *
     */
    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun applyStickers() {
        compositeDisposable.clear()
        val saveStickerDisposable = applyStickerToImage(
            activity?.getMainBit())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loadingDialog?.show() }
            .doFinally { loadingDialog?.dismiss() }
            .subscribe({ bitmap: Bitmap? ->
                if (bitmap == null) {
                    return@subscribe
                }
                stickerView?.clear()
                activity?.changeMainBitmap(bitmap, true)
                backToMain()
            }
            ) {
                Toast.makeText(getActivity(),
                    R.string.error,
                    Toast.LENGTH_SHORT).show()
            }
        compositeDisposable.add(saveStickerDisposable)
    }

    private fun applyStickerToImage(mainBitmap: Bitmap?): Single<Bitmap> {
        return Single.fromCallable {
            val context: ImageEditorActivity = requireActivity() as ImageEditorActivity
            val touchMatrix: Matrix = context.mPhotograph?.getImageViewMatrix()!!
            val resultBitmap = Bitmap.createBitmap(mainBitmap!!).copy(
                Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)
            val data = FloatArray(9)
            touchMatrix.getValues(data)
            val cal = Matrix3(data)
            val inverseMatrix: Matrix3 = cal.inverseMatrix()
            val m = Matrix()
            m.setValues(inverseMatrix.values)
            handleImage(canvas, m)
            resultBitmap
        }
    }

    private fun handleImage(canvas: Canvas, m: Matrix) {
        val addItems = (stickerView ?: return).getBank()
        for (id in addItems.keys) {
            val item = addItems[id]
            ((item ?: return).matrix ?: return).postConcat(m)
            canvas.drawBitmap(item.bitmap ?: return, item.matrix ?: return, null)
        }
    }

    /**
     *
     */
    fun newInstance(): StickerFragment {
        return StickerFragment()
    }
}
