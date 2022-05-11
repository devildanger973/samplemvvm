package paint

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import crop.BaseEditFragment
import crop.ModuleConfig
import crop.OnLoadingDialogListener
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

class PaintFragment : BaseEditFragment(), BrushConfigDialog.Properties,
    EraserConfigDialog.Properties, View.OnClickListener {
    val INDEX: Int = ModuleConfig.INDEX_PAINT
    private lateinit var root: View
    private var backToMenu: View? = null
    private var eraserView: View? = null
    private var brushView: View? = null
    private var setting: View? = null
    private var customPaintView: CustomPaintView? = null
    private var brushConfigDialog: BrushConfigDialog? = null
    private var eraserConfigDialog: EraserConfigDialog? = null
    private val MAX_ALPHA = 255f
    private val INITIAL_WIDTH = 50f
    private var isEraser = false
    private var brushColor = Color.WHITE
    private var brushSize: Float = INITIAL_WIDTH
    private var eraserSize: Float = INITIAL_WIDTH
    private var brushAlpha: Float = MAX_ALPHA
    private val MAX_PERCENT = 100f
    private val compositeDisposable = CompositeDisposable()
    private var loadingDialogListener: OnLoadingDialogListener? = null
    private var loadingDialog: Dialog? = null

    companion object {
        fun newInstance(): PaintFragment? {
            return PaintFragment()
        }
    }

    override fun onShow() {
        /*activity?.mode = (ensureEditActivity() ?: return).MODE_PAINT
        activity?.mPhotograph?.setImageBitmap(ensureEditActivity()?.getMainBit())
        customPaintView?.visibility = View.VISIBLE*/
    }

    override fun backToMain() {
        activity?.mode = ensureEditActivity()!!.MODE_NONE
        activity?.mPhotograph?.visibility = View.VISIBLE
        customPaintView!!.reset()
        customPaintView!!.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        root = inflater.inflate(R.layout.paint, container, false)
        loadingDialog = BaseActivity.getLoadingDialog(getActivity(),
            "Loading…",
            false)
        val backBtn: View = root.findViewById(R.id.back_to_main)
        backToMenu = root.findViewById(R.id.back_to_main)
        eraserView = root.findViewById<LinearLayout>(R.id.eraser_btn)
        brushView = root.findViewById<LinearLayout>(R.id.brush_btn)
        setting = root.findViewById<LinearLayout>(R.id.settings)
        customPaintView = ensureEditActivity()?.paintView
        apply()
        setupOptionsConfig()
        startPaint()
        initStroke()
        backBtn.setOnClickListener { backToMain() }
        return root
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            viewImage()
        }
    }

    private fun apply() {
        val applyBtn: ImageView = root.findViewById(R.id.apply_paint)
        applyBtn.setOnClickListener {
            savePaintImage()

        }
        loadingDialogListener = ensureEditActivity()

    }

    private fun viewImage() {
        activity?.mode = (ensureEditActivity() ?: return).MODE_PAINT
        activity?.mPhotograph?.setImageBitmap(activity?.getMainBit())
        customPaintView?.visibility = View.VISIBLE
    }

    private fun startPaint() {
        brushView?.setOnClickListener(this)
        eraserView?.setOnClickListener(this)
        setting?.setOnClickListener(this)
    }

    private fun initStroke() {
        customPaintView?.setWidth(INITIAL_WIDTH)
        customPaintView?.setColor(Color.WHITE)
        customPaintView?.setStrokeAlpha(MAX_ALPHA)
        customPaintView?.setEraserStrokeWidth(INITIAL_WIDTH)
    }


    private fun setupOptionsConfig() {
        brushConfigDialog = BrushConfigDialog()
        brushConfigDialog?.setPropertiesChangeListener(this)
        eraserConfigDialog = EraserConfigDialog()
        eraserConfigDialog?.setPropertiesChangeListener(this)
    }

    private fun showDialog(dialogFragment: BottomSheetDialogFragment) {
        val tag = dialogFragment.tag

        // Avoid IllegalStateException "Fragment already added"
        if (dialogFragment.isAdded) return
        dialogFragment.show(requireFragmentManager(), tag)
        if (isEraser) {
            updateEraserSize()
        } else {
            updateBrushParams()
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    fun savePaintImage() {
        val applyPaintDisposable = applyPaint(activity?.getMainBit()!!)
            ?.flatMap(Function<Bitmap, SingleSource<out Bitmap>> flatMap@{ bitmap: Bitmap? ->
                if (bitmap == null) {
                    return@flatMap Single.error<Bitmap>(Throwable("Error occurred while applying paint"))
                } else {
                    return@flatMap Single.just(bitmap)
                }
            })
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { _: Disposable? -> loadingDialog?.show() }
            ?.doFinally { loadingDialog?.dismiss() }
            ?.subscribe({ bitmap: Bitmap? ->
                customPaintView?.reset()
                activity?.changeMainBitmap(bitmap, true)
                backToMain()
            }) { _: Throwable? -> }
        compositeDisposable.add(applyPaintDisposable ?: return)
    }

    private fun applyPaint(mainBitmap: Bitmap): Single<Bitmap>? {
        return Single.fromCallable {
            val touchMatrix: Matrix = activity?.mPhotograph?.getImageViewMatrix()!!
            val resultBit = Bitmap.createBitmap(mainBitmap).copy(
                Bitmap.Config.ARGB_8888, true
            )
            val canvas = Canvas(resultBit)
            val data = FloatArray(9)
            touchMatrix.getValues(data)
            val cal = Matrix3(data)
            val inverseMatrix: Matrix3 = cal.inverseMatrix()
            val matrix = Matrix()
            matrix.setValues(inverseMatrix.values)
            handleImage(canvas, matrix)
            resultBit
        }
    }

    private fun handleImage(canvas: Canvas, matrix: Matrix) {
        val f = FloatArray(9)
        matrix.getValues(f)
        val dx = f[Matrix.MTRANS_X].toInt()
        val dy = f[Matrix.MTRANS_Y].toInt()
        val scale_x = f[Matrix.MSCALE_X]
        val scale_y = f[Matrix.MSCALE_Y]
        canvas.save()
        canvas.translate(dx.toFloat(), dy.toFloat())
        canvas.scale(scale_x, scale_y)
        if (customPaintView?.getPaintBit() != null) {
            canvas.drawBitmap(customPaintView?.getPaintBit() ?: return, 0f, 0f, null)
        }
        canvas.restore()
    }

    override fun onColorChanged(colorCode: Int) {
        brushColor = colorCode
        updateBrushParams()
    }

    override fun onOpacityChanged(opacity: Int) {
        brushAlpha = opacity / MAX_PERCENT * MAX_ALPHA
        updateBrushParams()
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        if (isEraser) {
            eraserSize = brushSize.toFloat()
            updateEraserSize()
        } else {
            this.brushSize = brushSize.toFloat()
            updateBrushParams()
        }
    }

    private fun updateBrushParams() {
        (customPaintView ?: return).setColor(brushColor)
        customPaintView?.setWidth(brushSize)
        customPaintView?.setStrokeAlpha(brushAlpha)
    }

    private fun updateEraserSize() {
        (customPaintView ?: return).setEraserStrokeWidth(eraserSize)
    }

    private fun toggleButtons() {
        isEraser = !isEraser
        (customPaintView ?: return).setEraser(isEraser)
        ((eraserView ?: return).findViewById<View>(R.id.eraser_icon) as ImageView).setImageResource(
            if (isEraser) R.drawable.ic_eraser_enabled else R.drawable.ic_eraser_disabled)
        ((brushView ?: return).findViewById<View>(R.id.brush_icon) as ImageView).setImageResource(if (isEraser) R.drawable.ic_brush_grey_24dp else R.drawable.ic_brush_white_24dp)
    }

    override fun onClick(view: View?) {
        if (view === backToMenu) {
            backToMain()
        } else if (view === eraserView) {
            if (!isEraser) {
                toggleButtons()
            }
        } else if (view === brushView) {
            if (isEraser) {
                toggleButtons()
            }
        } else if (view == setting) {
            showDialog((if (isEraser) eraserConfigDialog else brushConfigDialog)!!)
        }
    }

}