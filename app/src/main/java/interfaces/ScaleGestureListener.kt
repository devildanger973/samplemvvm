package interfaces

import android.view.View

class ScaleGestureListener internal constructor(private val multiTouchListener: MultiTouchListener) :
    OnScaleGestureListener {
    private var pivotX = 0f
    private var pivotY = 0f
    private val prevSpanVector = Vector2D()
    override fun onScale(view: View?, detectorScale: ScaleGestureDetector?): Boolean {
        val info = TransformInfo()
        info.deltaScale = if (multiTouchListener.isScaleEnabled) detectorScale!!.getScaleFactor() else 1.0f
        info.deltaAngle = if (multiTouchListener.isRotateEnabled) VectorAngle.getAngle(
            prevSpanVector,
            detectorScale!!.currentSpanVector) else 0.0f
        info.deltaX =
            if (multiTouchListener.isTranslateEnabled) detectorScale!!.focusX - pivotX else 0.0f
        info.deltaY =
            if (multiTouchListener.isTranslateEnabled) detectorScale!!.focusY - pivotY else 0.0f
        info.pivotX = pivotX
        info.pivotY = pivotY
        info.minimumScale = multiTouchListener.minimumScale
        info.maximumScale = multiTouchListener.maximumScale
        multiTouchListener.move(view!!, info)
        return !multiTouchListener.isTextPinchZoomable
    }

    override fun onScaleBegin(view: View?, detectorScale: ScaleGestureDetector?): Boolean {
        pivotX = detectorScale!!.focusX
        pivotY = detectorScale!!.focusY
        prevSpanVector.set(detectorScale.currentSpanVector)
        return multiTouchListener.isTextPinchZoomable
    }

    override fun onScaleEnd(view: View?, detector: ScaleGestureDetector?) {}
}
