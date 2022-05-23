package add.text

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

class TextStickerView : RelativeLayout {
    var bitmapHolderImageView: ImageView? = null
        private set

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {}
    fun updateImageBitmap(bitmap: Bitmap?) {
        if (bitmapHolderImageView != null) {
            removeView(bitmapHolderImageView)
        }
        bitmapHolderImageView = ImageView(context)

        //Setup image attributes
        val imageViewParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        imageViewParams.addRule(CENTER_IN_PARENT, TRUE)
        bitmapHolderImageView!!.layoutParams = imageViewParams
        bitmapHolderImageView!!.scaleType = ImageView.ScaleType.FIT_CENTER
        bitmapHolderImageView!!.adjustViewBounds = true
        bitmapHolderImageView!!.isDrawingCacheEnabled = true
        bitmapHolderImageView!!.setImageBitmap(bitmap)
        addView(bitmapHolderImageView)
    }
}
