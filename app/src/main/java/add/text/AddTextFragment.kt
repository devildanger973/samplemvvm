package add.text

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.myapplication.ImageEditorActivity
import com.example.myapplication.R
import crop.BaseEditFragment
import crop.OnMainBitmapChangeListener
import interfaces.MultiTouchListener
import interfaces.OnGestureControl
import interfaces.OnMultiTouchListener
import interfaces.OnPhotoEditorListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 *
 */
class AddTextFragment : BaseEditFragment(), OnPhotoEditorListener, View.OnClickListener,
    OnMainBitmapChangeListener, OnMultiTouchListener {
    private var textStickersParentView: TextStickerView? = null
    private var zoomLayout: ZoomLayout? = null
    private var inputMethodManager: InputMethodManager? = null
    private lateinit var root: View
    private var addedViews: MutableList<View>? = null
    private var textEditorDialogFragment: TextEditorDialogFragment? = null
    private val compositeDisposable = CompositeDisposable()


    override fun onResume() {
        super.onResume()
        if (isVisible) {
            view()
        }
    }

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        root = inflater.inflate(R.layout.fragment_edit_image_add_text, container, false)
        val editImageActivity: ImageEditorActivity? = ensureEditActivity()

        inputMethodManager =
            editImageActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        textStickersParentView = editImageActivity.findViewById(R.id.text_sticker_panel)
        textStickersParentView!!.isDrawingCacheEnabled = true
        addedViews = ArrayList()

        zoomLayout = editImageActivity.findViewById(R.id.text_sticker_panel_frame)

        val backToMenu: View = root.findViewById(R.id.back_to_main)
        backToMenu.setOnClickListener { backToMain() }

        val addTextButton: LinearLayout = root.findViewById(R.id.add_text_btn)
        addTextButton.setOnClickListener {
            textEditorDialogFragment =
                TextEditorDialogFragment.show(activity ?: return@setOnClickListener)
            textEditorDialogFragment?.setOnTextEditorListener(object :
                TextEditorDialogFragment.SetOnTextEditorListener {
                override fun setOnTextEditorListener(inputText: String?, colorCode: Int) {
                    addText(inputText ?: return, colorCode)
                }

            })

        }
        val applyAddText: ImageView = root.findViewById(R.id.apply_add_text)
        applyAddText.setOnClickListener { applyTextImage() }
        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addText(text: String, colorCodeTextView: Int) {
        val textStickerView: View = getTextStickerLayout() ?: return
        val textInputTv = textStickerView.findViewById<TextView>(R.id.text_sticker_tv)
        val imgClose = textStickerView.findViewById<ImageView>(R.id.sticker_delete_btn)
        val frameBorder = textStickerView.findViewById<FrameLayout>(R.id.sticker_border)
        textInputTv.text = text
        textInputTv.setTextColor(colorCodeTextView)
        textInputTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
            resources.getDimension(R.dimen.text_sticker_size))
        val multiTouchListener = MultiTouchListener(
            imgClose,
            textStickersParentView ?: return,
            activity?.mPhotograph ?: return,
            this, context)
        multiTouchListener.setOnGestureControl(object : OnGestureControl {
            var isDownAlready = false
            override fun onClick() {
                val isBackgroundVisible = frameBorder.tag != null && frameBorder.tag as Boolean
                if (isBackgroundVisible && !isDownAlready) {
                    val textInput = textInputTv.text.toString()
                    val currentTextColor = textInputTv.currentTextColor
                    showTextEditDialog(textStickerView, textInput, currentTextColor)
                }
            }

            override fun onDown() {
                val isBackgroundVisible = frameBorder.tag != null && frameBorder.tag as Boolean
                if (!isBackgroundVisible) {
                    frameBorder.setBackgroundResource(R.drawable.background_border)
                    imgClose.visibility = View.VISIBLE
                    frameBorder.tag = true
                    updateViewsBordersVisibilityExcept(textStickerView)
                    isDownAlready = true
                } else {
                    isDownAlready = false
                }
            }

            override fun onLongClick() {}
        })
        textStickerView.setOnTouchListener(multiTouchListener)
        addViewToParent(textStickerView)
    }

    private fun addViewToParent(view: View) {
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        textStickersParentView?.addView(view, params)
        addedViews?.add(view)
        updateViewsBordersVisibilityExcept(view)
    }

    private fun showTextEditDialog(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment =
            TextEditorDialogFragment.show(activity ?: return, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditorDialogFragment.SetOnTextEditorListener {
            override fun setOnTextEditorListener(inputText: String?, colorCode: Int) {
                editText(rootView,
                    inputText ?: return,
                    colorCode)
            }
        })
    }

    private fun applyTextImage() {
        // Hide borders of all stickers before save
        updateViewsBordersVisibilityExcept(null)
        val applyTextDisposable = Observable.fromCallable {
            getFinalBitmapFromView(textStickersParentView!!)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { bitmap: Bitmap? ->
                    if ((addedViews ?: return@subscribe).size > 0) {
                        (activity ?: return@subscribe).changeMainBitmap(bitmap, true)
                    }
                    backToMain()
                }
            ) { e: Throwable ->
                e.printStackTrace()
                backToMain()
                Toast.makeText(context,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT).show()
            }
        compositeDisposable.add(applyTextDisposable)
    }


    private fun getFinalBitmapFromView(view: View): Bitmap? {
        val finalBitmap = view.drawingCache
        val resultBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val textStickerHeightCenterY = textStickersParentView?.height?.div(2)
        val textStickerWidthCenterX = textStickersParentView?.width?.div(2)
        val imageViewHeight: Int =
            (textStickersParentView?.bitmapHolderImageView ?: return null).height
        val imageViewWidth: Int =
            (textStickersParentView?.bitmapHolderImageView ?: return null).width

        // Crop actual image from textStickerView
        return Bitmap.createBitmap(resultBitmap,
            (textStickerWidthCenterX ?: return null) - imageViewWidth / 2,
            (textStickerHeightCenterY ?: return null) - imageViewHeight / 2,
            imageViewWidth,
            imageViewHeight)
    }

    private fun editText(view: View, inputText: String, colorCode: Int) {
        val inputTextView = view.findViewById<TextView>(R.id.text_sticker_tv)
        if (inputTextView != null && (addedViews ?: return).contains(view) && !TextUtils.isEmpty(
                inputText)
        ) {
            inputTextView.text = inputText
            inputTextView.setTextColor(colorCode)
            textStickersParentView?.updateViewLayout(view, view.layoutParams)
            val i = (addedViews ?: return).indexOf(view)
            if (i > -1) (addedViews ?: return)[i] = view
        }
    }


    private fun getTextStickerLayout(): View? {
        val layoutInflater = LayoutInflater.from(context)
        val rootView = layoutInflater.inflate(R.layout.view_text_sticker_item, null)
        val txtText = rootView.findViewById<TextView>(R.id.text_sticker_tv)
        if (txtText != null) {
            txtText.gravity = Gravity.CENTER
            val imgClose = rootView.findViewById<ImageView>(R.id.sticker_delete_btn)
            imgClose?.setOnClickListener { _: View? ->
                deleteViewFromParent(rootView)
            }
        }
        return rootView
    }

    private fun deleteViewFromParent(view: View) {
        (textStickersParentView ?: return).removeView(view)
        addedViews?.remove(view)
        (textStickersParentView ?: return).invalidate()
        updateViewsBordersVisibilityExcept(null)
    }

    private fun updateViewsBordersVisibilityExcept(keepView: View?) {
        for (view in addedViews ?: return) {
            if (view !== keepView) {
                val border = view.findViewById<FrameLayout>(R.id.sticker_border)
                border.setBackgroundResource(0)
                val closeBtn = view.findViewById<ImageView>(R.id.sticker_delete_btn)
                closeBtn.visibility = View.GONE
                border.tag = false
            }
        }
    }

    private fun view() {
        (activity ?: return).mode = (activity ?: return).MODE_TEXT
        activity?.mPhotograph?.visibility = View.GONE
        textStickersParentView?.updateImageBitmap((activity ?: return).getMainBit())
        textStickersParentView?.visibility = View.VISIBLE

        autoScaleImageToFitBounds()
    }

    override fun onShow() {

    }

    private fun autoScaleImageToFitBounds() {
        (textStickersParentView ?: return).viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                (textStickersParentView ?: return).viewTreeObserver.removeOnGlobalLayoutListener(
                    this)
                scaleImage()
            }
        })
    }

    private fun scaleImage() {
        val zoomLayoutWidth: Float = (zoomLayout?.width ?: return).toFloat()
        val zoomLayoutHeight: Float = (zoomLayout ?: return).height.toFloat()
        val imageViewWidth = (textStickersParentView ?: return).width.toFloat()
        val imageViewHeight = (textStickersParentView ?: return).height.toFloat()

        // To avoid divideByZero exception
        if (imageViewHeight != 0f && imageViewWidth != 0f && zoomLayoutHeight != 0f && zoomLayoutWidth != 0f) {
            val offsetFactorX = zoomLayoutWidth / imageViewWidth
            val offsetFactorY = zoomLayoutHeight / imageViewHeight
            val scaleFactor = Math.min(offsetFactorX, offsetFactorY)
            zoomLayout?.setChildScale(scaleFactor)
        }
    }

    private fun clearAllStickers() {
        textStickersParentView?.removeAllViews()
    }

    override fun backToMain() {
        hideInput()
        clearAllStickers()
        (activity ?: return).mode = (activity?.MODE_NONE ?: return)
        activity?.mPhotograph?.visibility = View.VISIBLE
        textStickersParentView?.visibility = View.GONE
    }

    private fun isInputMethodShow(): Boolean {
        return inputMethodManager!!.isActive
    }

    private fun hideInput() {
        if (activity != null && (activity ?: return).currentFocus != null && isInputMethodShow()) {
            inputMethodManager?.hideSoftInputFromWindow((activity
                ?: return).currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     *
     */
    override fun onMainBitmapChange() {
        (textStickersParentView ?: return).updateImageBitmap((activity ?: return).getMainBit())
    }

    /**
     *
     */
    override fun onRemoveViewListener(removedView: View?) {
    }

    /**
     *
     */
    override fun onAddViewListener(numberOfAddedViews: Int) {
    }

    /**
     *
     */
    override fun onRemoveViewListener(numberOfAddedViews: Int) {
    }

    /**
     *
     */
    override fun onStartViewChangeListener() {
    }

    /**
     *
     */
    override fun onStopViewChangeListener() {
    }

    /**
     *
     */
    override fun onClick(p0: View?) {
    }
}
