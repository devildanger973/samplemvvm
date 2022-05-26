package sticker

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.myapplication.R

/**
 *
 */
class StickerItem internal constructor(context: Context) {
    /**
     *
     */
    var bitmap: Bitmap? = null

    /**
     *
     */
    var dstRect: RectF? = null
    private var helpToolsRect: Rect? = null
    private var deleteRect: RectF? = null
    private var rotateRect: RectF? = null
    private var helpBox: RectF? = null

    /**
     *
     */
    var matrix: Matrix? = null
    private var roatetAngle = 0f

    /**
     *
     */
    var isDrawHelpTool = false
    private val paint = Paint()
    private val helpBoxPaint = Paint()
    private var initWidth = 0f

    /**
     *
     */
    var detectRotateRect: RectF? = null

    /**
     *
     */
    var detectDeleteRect: RectF? = null

    /**
     *
     */
    fun init(addBit: Bitmap, parentView: View) {
        bitmap = addBit
        val bitWidth = Math.min(addBit.width, parentView.width shr 1)
        val bitHeight = bitWidth * addBit.height / addBit.width
        val left = (parentView.width shr 1) - (bitWidth shr 1)
        val top = (parentView.height shr 1) - (bitHeight shr 1)
        dstRect = RectF(left.toFloat(), top.toFloat(), (left + bitWidth).toFloat(),
            (top + bitHeight).toFloat())
        matrix = Matrix()
        matrix!!.postTranslate(dstRect!!.left, dstRect!!.top)
        matrix!!.postScale(bitWidth.toFloat() / addBit.width,
            bitHeight.toFloat() / addBit.height, dstRect!!.left,
            dstRect!!.top)
        initWidth = dstRect!!.width()
        isDrawHelpTool = true
        helpBox = RectF(dstRect)
        updateHelpBoxRect()
        helpToolsRect = Rect(0, 0, deleteBit!!.width,
            deleteBit!!.height)
        deleteRect = RectF(helpBox!!.left - BUTTON_WIDTH,
            helpBox!!.top
                    - BUTTON_WIDTH, helpBox!!.left + BUTTON_WIDTH, (helpBox!!.top
                    + BUTTON_WIDTH))
        rotateRect = RectF(helpBox!!.right - BUTTON_WIDTH, (helpBox!!.bottom
                - BUTTON_WIDTH), helpBox!!.right + BUTTON_WIDTH, (helpBox!!.bottom
                + BUTTON_WIDTH))
        detectRotateRect = RectF(rotateRect)
        detectDeleteRect = RectF(deleteRect)
    }

    private fun updateHelpBoxRect() {
        helpBox!!.left -= HELP_BOX_PAD.toFloat()
        helpBox!!.right += HELP_BOX_PAD.toFloat()
        helpBox!!.top -= HELP_BOX_PAD.toFloat()
        helpBox!!.bottom += HELP_BOX_PAD.toFloat()
    }

    /**
     *
     */
    fun updatePos(dx: Float, dy: Float) {
        matrix!!.postTranslate(dx, dy)
        dstRect!!.offset(dx, dy)
        helpBox!!.offset(dx, dy)
        deleteRect!!.offset(dx, dy)
        rotateRect!!.offset(dx, dy)
        detectRotateRect!!.offset(dx, dy)
        detectDeleteRect!!.offset(dx, dy)
    }

    /**
     *
     */
    fun updateRotateAndScale(
        oldx: Float, oldy: Float,
        dx: Float, dy: Float,
    ) {
        val c_x = dstRect!!.centerX()
        val c_y = dstRect!!.centerY()
        val x = detectRotateRect!!.centerX()
        val y = detectRotateRect!!.centerY()
        val n_x = x + dx
        val n_y = y + dy
        val xa = x - c_x
        val ya = y - c_y
        val xb = n_x - c_x
        val yb = n_y - c_y
        val srcLen = Math.sqrt((xa * xa + ya * ya).toDouble()).toFloat()
        val curLen = Math.sqrt((xb * xb + yb * yb).toDouble()).toFloat()
        val scale = curLen / srcLen
        val newWidth = dstRect!!.width() * scale
        if (newWidth / initWidth < MIN_SCALE) {
            return
        }
        matrix!!.postScale(scale, scale, dstRect!!.centerX(),
            dstRect!!.centerY())
        RectUtil.scaleRect(dstRect!!, scale)
        helpBox!!.set((dstRect)!!)
        updateHelpBoxRect()
        rotateRect!!.offsetTo(helpBox!!.right - BUTTON_WIDTH, (helpBox!!.bottom
                - BUTTON_WIDTH))
        deleteRect!!.offsetTo(helpBox!!.left - BUTTON_WIDTH, (helpBox!!.top
                - BUTTON_WIDTH))
        detectRotateRect!!.offsetTo(helpBox!!.right - BUTTON_WIDTH, (helpBox!!.bottom
                - BUTTON_WIDTH))
        detectDeleteRect!!.offsetTo(helpBox!!.left - BUTTON_WIDTH, (helpBox!!.top
                - BUTTON_WIDTH))
        val cos = ((xa * xb + ya * yb) / (srcLen * curLen)).toDouble()
        if (cos > 1 || cos < -1) return
        var angle = Math.toDegrees(Math.acos(cos)).toFloat()
        val calMatrix = xa * yb - xb * ya
        val flag = if (calMatrix > 0) 1 else -1
        angle = flag * angle
        roatetAngle += angle
        matrix!!.postRotate(angle, dstRect!!.centerX(),
            dstRect!!.centerY())
        RectUtil.rotateRect(detectRotateRect!!, dstRect!!.centerX(),
            dstRect!!.centerY(), roatetAngle)
        RectUtil.rotateRect(detectDeleteRect!!, dstRect!!.centerX(),
            dstRect!!.centerY(), roatetAngle)
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap((bitmap)!!, (matrix)!!, null)
        if (isDrawHelpTool) {
            canvas.save()
            canvas.rotate(roatetAngle, helpBox!!.centerX(), helpBox!!.centerY())
            canvas.drawRoundRect((helpBox)!!, 10f, 10f, helpBoxPaint)
            canvas.drawBitmap((deleteBit)!!, helpToolsRect,
                (deleteRect)!!, null)
            canvas.drawBitmap((rotateBit)!!, helpToolsRect,
                (rotateRect)!!, null)
            canvas.restore()
        }
    }

    companion object {
        private val MIN_SCALE = 0.15f
        private val HELP_BOX_PAD = 25
        private val BORDER_STROKE_WIDTH = 8
        private val BUTTON_WIDTH: Int = Constants.STICKER_BTN_HALF_SIZE
        private var deleteBit: Bitmap? = null
        private var rotateBit: Bitmap? = null
    }

    init {
        helpBoxPaint.color = Color.WHITE
        helpBoxPaint.style = Paint.Style.STROKE
        helpBoxPaint.isAntiAlias = true
        helpBoxPaint.strokeWidth = BORDER_STROKE_WIDTH.toFloat()
        val dstPaint = Paint()
        dstPaint.color = Color.RED
        dstPaint.alpha = 120
        val greenPaint = Paint()
        greenPaint.color = Color.GREEN
        greenPaint.alpha = 120
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_close)
        }
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_resize)
        }
    }
}
