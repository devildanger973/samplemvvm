package interfaces

import android.content.Context
import android.graphics.Rect
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class MultiTouchListener(
    deleteView: View?, parentView: RelativeLayout,
    photoEditImageView: ImageView,
    onPhotoEditorListener: OnPhotoEditorListener?, context: Context?,
) :
    OnTouchListener {
    private val gestureListener: GestureDetector
    var isRotateEnabled = true
    var isTranslateEnabled = true
    var isScaleEnabled = true
    var minimumScale = 0.2f
    var maximumScale = 10.0f
    private var activePointerId = INVALID_POINTER_ID
    private var prevX = 0f
    private var prevY = 0f
    private var prevRawX = 0f
    private var prevRawY = 0f
    private val scaleGestureDetector: ScaleGestureDetector
    private val location = IntArray(2)
    private var outRect: Rect? = null
    private val deleteView: View?
    private val photoEditImageView: ImageView
    private val parentView: RelativeLayout
    private var onMultiTouchListener: OnMultiTouchListener? = null
    private var onGestureControl: OnGestureControl? = null
    var isTextPinchZoomable = true
    private val onPhotoEditorListener: OnPhotoEditorListener?
    private fun adjustAngle(degrees: Float): Float {
        var degrees = degrees
        if (degrees > 180.0f) {
            degrees -= 360.0f
        } else if (degrees < -180.0f) {
            degrees += 360.0f
        }
        return degrees
    }

    fun move(view: View, info: TransformInfo) {
        computeRenderOffset(view, info.pivotX, info.pivotY)
        adjustTranslation(view, info.deltaX, info.deltaY)
        var scale: Float = view.scaleX * info.deltaScale
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale))
        view.scaleX = scale
        view.scaleY = scale
        val rotation = adjustAngle(view.rotation + info.deltaAngle)
        view.rotation = rotation
    }

    private fun adjustTranslation(view: View, deltaX: Float, deltaY: Float) {
        val deltaVector = floatArrayOf(deltaX, deltaY)
        view.matrix.mapVectors(deltaVector)
        view.translationX = view.translationX + deltaVector[0]
        view.translationY = view.translationY + deltaVector[1]
    }

    private fun computeRenderOffset(view: View, pivotX: Float, pivotY: Float) {
        if (view.pivotX == pivotX && view.pivotY == pivotY) {
            return
        }
        val prevPoint = floatArrayOf(0.0f, 0.0f)
        view.matrix.mapPoints(prevPoint)
        view.pivotX = pivotX
        view.pivotY = pivotY
        val currPoint = floatArrayOf(0.0f, 0.0f)
        view.matrix.mapPoints(currPoint)
        val offsetX = currPoint[0] - prevPoint[0]
        val offsetY = currPoint[1] - prevPoint[1]
        view.translationX = view.translationX - offsetX
        view.translationY = view.translationY - offsetY
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(view, event)
        gestureListener.onTouchEvent(event)
        if (!isTranslateEnabled) {
            return true
        }
        val action = event.action
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        when (action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                prevX = event.x
                prevY = event.y
                prevRawX = event.rawX
                prevRawY = event.rawY
                activePointerId = event.getPointerId(0)
                view.bringToFront()
                firePhotoEditorSDKListener(view, true)
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndexMove = event.findPointerIndex(activePointerId)
                if (pointerIndexMove != -1) {
                    val currX = event.getX(pointerIndexMove)
                    val currY = event.getY(pointerIndexMove)
                    if (!scaleGestureDetector.isInProgress) {
                        adjustTranslation(view, currX - prevX, currY - prevY)
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> activePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_UP -> {
                activePointerId = INVALID_POINTER_ID
                if (deleteView != null && isViewInBounds(deleteView, x, y)) {
                    if (onMultiTouchListener != null) onMultiTouchListener!!.onRemoveViewListener(
                        view)
                } else if (!isViewInBounds(photoEditImageView, x, y)) {
                    view.animate().translationY(0f).translationY(0f)
                }
                firePhotoEditorSDKListener(view, false)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndexPointerUp =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndexPointerUp)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndexPointerUp == 0) 1 else 0
                    prevX = event.getX(newPointerIndex)
                    prevY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    private fun firePhotoEditorSDKListener(view: View, isStart: Boolean) {
        if (view is TextView) {
            if (onMultiTouchListener != null) {
                if (onPhotoEditorListener != null) {
                    if (isStart) onPhotoEditorListener.onStartViewChangeListener() else onPhotoEditorListener.onStopViewChangeListener()
                }
            } else {
                if (onPhotoEditorListener != null) {
                    if (isStart) onPhotoEditorListener.onStartViewChangeListener() else onPhotoEditorListener.onStopViewChangeListener()
                }
            }
        } else {
            if (onPhotoEditorListener != null) {
                if (isStart) onPhotoEditorListener.onStartViewChangeListener() else onPhotoEditorListener.onStopViewChangeListener()
            }
        }
    }

    private fun isViewInBounds(view: View, x: Int, y: Int): Boolean {
        view.getDrawingRect(outRect)
        view.getLocationOnScreen(location)
        outRect!!.offset(location[0], location[1])
        return outRect!!.contains(x, y)
    }

    private fun setOnMultiTouchListener(onMultiTouchListener: OnMultiTouchListener) {
        this.onMultiTouchListener = onMultiTouchListener
    }

    fun setOnGestureControl(onGestureControl: OnGestureControl?) {
        this.onGestureControl = onGestureControl
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (onGestureControl != null) {
                onGestureControl!!.onClick()
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            if (onGestureControl != null) {
                onGestureControl!!.onDown()
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            if (onGestureControl != null) {
                onGestureControl!!.onLongClick()
            }
        }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        scaleGestureDetector = ScaleGestureDetector(ScaleGestureListener(this))
        gestureListener = GestureDetector(context, GestureListener())
        this.deleteView = deleteView
        this.parentView = parentView
        this.photoEditImageView = photoEditImageView
        this.onPhotoEditorListener = onPhotoEditorListener
        outRect = if (deleteView != null) {
            Rect(deleteView.left, deleteView.top,
                deleteView.right, deleteView.bottom)
        } else {
            Rect(0, 0, 0, 0)
        }
    }
}
