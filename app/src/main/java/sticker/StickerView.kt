package sticker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 *
 */
class StickerView : View {
    private var imageCount = 0
    private var mContext: Context? = null
    private var currentStatus = 0
    private var currentItem: StickerItem? = null
    private var oldx = 0f
    private var oldy = 0f
    private val rectPaint = Paint()
    private val boxPaint = Paint()
    private val bank: LinkedHashMap<Int, StickerItem> = LinkedHashMap<Int, StickerItem>()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        currentStatus = STATUS_IDLE
        rectPaint.color = Color.RED
        rectPaint.alpha = 100
    }

    fun addBitImage(addBit: Bitmap?) {
        val item = StickerItem(this.context)
        item.init(addBit!!, this)
        if (currentItem != null) {
            currentItem!!.isDrawHelpTool = false
        }
        bank[++imageCount] = item
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (id in bank.keys) {
            val item: StickerItem? = bank[id]
            item?.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var ret = super.onTouchEvent(event)
        val action = event.action
        val x = event.x
        val y = event.y
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                var deleteId = -1
                for (id in bank.keys) {
                    val item: StickerItem? = bank[id]
                    if (item?.detectDeleteRect!!.contains(x, y)) {
                        // ret = true;
                        deleteId = id
                        currentStatus = STATUS_DELETE
                    } else if (item?.detectRotateRect!!.contains(x, y)) {
                        ret = true
                        if (currentItem != null) {
                            currentItem?.isDrawHelpTool = false
                        }
                        currentItem = item
                        currentItem?.isDrawHelpTool = true
                        currentStatus = STATUS_ROTATE
                        oldx = x
                        oldy = y
                    } else if (item.dstRect!!.contains(x, y)) {
                        ret = true
                        if (currentItem != null) {
                            currentItem?.isDrawHelpTool = false
                        }
                        currentItem = item
                        currentItem?.isDrawHelpTool = true
                        currentStatus = STATUS_MOVE
                        oldx = x
                        oldy = y
                    }
                }
                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {
                    currentItem?.isDrawHelpTool = false
                    currentItem = null
                    invalidate()
                }
                if (deleteId > 0 && currentStatus == STATUS_DELETE) {
                    bank.remove(deleteId)
                    currentStatus = STATUS_IDLE
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                ret = true
                if (currentStatus == STATUS_MOVE) {
                    val dx = x - oldx
                    val dy = y - oldy
                    if (currentItem != null) {
                        currentItem?.updatePos(dx, dy)
                        invalidate()
                    }
                    oldx = x
                    oldy = y
                } else if (currentStatus == STATUS_ROTATE) {
                    val dx = x - oldx
                    val dy = y - oldy
                    if (currentItem != null) {
                        currentItem?.updateRotateAndScale(oldx, oldy, dx, dy)
                        invalidate()
                    }
                    oldx = x
                    oldy = y
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                ret = false
                currentStatus = STATUS_IDLE
            }
        }
        return ret
    }

    fun getBank(): LinkedHashMap<Int, StickerItem> {
        return bank
    }

    fun clear() {
        bank.clear()
        this.invalidate()
    }

    companion object {
        private const val STATUS_IDLE = 0
        private const val STATUS_MOVE = 1
        private const val STATUS_DELETE = 2
        private const val STATUS_ROTATE = 3
    }
}
