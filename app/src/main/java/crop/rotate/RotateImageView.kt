package crop.rotate

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView

class RotateImageView : ImageView {
    private var srcRect: Rect? = null
    private var dstRect: RectF? = null
    private var maxRect: Rect? = null
    private var bitmap: Bitmap? = null
    internal val matrix = Matrix()

    /**
     *
     */
    @get:Synchronized
    var scale: Float = 0f
        private set

    /**
     *
     */
    @get:Synchronized
    var rotateAngle: Float = 0f
        private set
    private val wrapRect = RectF()
    private var bottomPaint: Paint? = null
    private var originImageRect: RectF? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        srcRect = Rect()
        dstRect = RectF()
        maxRect = Rect()
        bottomPaint = PaintUtil.newRotateBottomImagePaint()
        originImageRect = RectF()
    }

    fun addBit(bit: Bitmap, imageRect: RectF?) {
        bitmap = bit
        srcRect!![0, 0, bitmap!!.width] = bitmap!!.height
        dstRect = imageRect
        originImageRect!![0f, 0f, bit.width.toFloat()] = bit.height.toFloat()
        this.invalidate()
    }

    fun rotateImage(angle: Float?) {
        rotateAngle = angle!!
        this.invalidate()
    }

    fun reset() {
        rotateAngle = 0f
        scale = 1f
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (bitmap == null) return
        (maxRect ?: return)[0, 0, width] = height
        calculateWrapBox()
        scale = 1f
        if (wrapRect.width() > width) {
            scale = width / wrapRect.width()
        }
        canvas.save()
        canvas.scale(
            scale, scale, (canvas.width shr 1).toFloat(), (
                    canvas.height shr 1).toFloat()
        )
        canvas.drawRect(wrapRect, bottomPaint ?: return)
        canvas.rotate(
            rotateAngle.toFloat(), (canvas.width shr 1).toFloat(), (
                    canvas.height shr 1).toFloat()
        )
        canvas.drawBitmap(bitmap ?: return, srcRect, dstRect!!, null)
        canvas.restore()
    }

    private fun calculateWrapBox() {
        wrapRect.set(dstRect ?: return)
        matrix.reset() // 重置矩阵为单位矩阵
        val centerX = width shr 1
        val centerY = height shr 1
        matrix.postRotate(rotateAngle.toFloat(), centerX.toFloat(), centerY.toFloat())
        matrix.mapRect(wrapRect)
    }

    /**
     *
     */
    open fun getImageNewRect(bit: Bitmap?): RectF? {
        val m = Matrix()
        originImageRect!![0f, 0f, bit?.width!!.toFloat()] = bit.height.toFloat()

        m.postRotate(rotateAngle, originImageRect!!.centerX(),
            originImageRect!!.centerY())
        m.mapRect(originImageRect)
        return originImageRect
    }
}
