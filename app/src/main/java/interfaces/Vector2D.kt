package interfaces

import android.graphics.PointF

class Vector2D : PointF() {
    fun normalize() {
        val length = Math.sqrt((x * x + y * y).toDouble()).toFloat()
        x /= length
        y /= length
    }
}
