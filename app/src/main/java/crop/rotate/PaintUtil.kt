package crop.rotate

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue

/**
 * Utility class for handling all of the Paint used to draw the CropOverlayView.
 */
object PaintUtil {
    // Private Constants ///////////////////////////////////////////////////////
    private const val DEFAULT_CORNER_COLOR = Color.WHITE
    private const val SEMI_TRANSPARENT = "#AAFFFFFF"
    private const val DEFAULT_BACKGROUND_COLOR_ID = "#B0000000"

    /**
     * Returns the value of the line thickness of the border
     *
     * @return Float equivalent to the line thickness
     */
    const val lineThickness = 3f

    /**
     * Returns the value of the corner thickness
     *
     * @return Float equivalent to the corner thickness
     */
    const val cornerThickness = 5f
    private const val DEFAULT_GUIDELINE_THICKNESS_PX = 1f
    // Public Methods //////////////////////////////////////////////////////////
    /**
     * Creates the Paint object for drawing the crop window border.
     *
     * @param context
     * the Context
     * @return new Paint object
     */
    fun newBorderPaint(context: Context): Paint {

        // Set the line thickness for the crop window border.
        val lineThicknessPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, lineThickness, context
                .resources.displayMetrics
        )
        val borderPaint = Paint()
        borderPaint.color = Color.parseColor(SEMI_TRANSPARENT)
        borderPaint.strokeWidth = lineThicknessPx
        borderPaint.style = Paint.Style.STROKE
        return borderPaint
    }

    /**
     * Creates the Paint object for drawing the crop window guidelines.
     *
     * @return the new Paint object
     */
    fun newGuidelinePaint(): Paint {
        val paint = Paint()
        paint.color = Color.parseColor(SEMI_TRANSPARENT)
        paint.strokeWidth = DEFAULT_GUIDELINE_THICKNESS_PX
        return paint
    }

    /**
     * Creates the Paint object for drawing the crop window guidelines.
     *
     * @return the new Paint object
     */
    fun newRotateBottomImagePaint(): Paint {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.strokeWidth = 3f
        return paint
    }

    /**
     * Creates the Paint object for drawing the translucent overlay outside the
     * crop window.
     *
     * @param context
     * the Context
     * @return the new Paint object
     */
    fun newBackgroundPaint(context: Context?): Paint {
        val paint = Paint()
        paint.color = Color.parseColor(DEFAULT_BACKGROUND_COLOR_ID)
        return paint
    }

    /**
     * Creates the Paint object for drawing the corners of the border
     *
     * @param context
     * the Context
     * @return the new Paint object
     */
    fun newCornerPaint(context: Context): Paint {

        // Set the line thickness for the crop window border.
        val lineThicknessPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, cornerThickness,
            context.resources.displayMetrics
        )
        val cornerPaint = Paint()
        cornerPaint.color = DEFAULT_CORNER_COLOR
        cornerPaint.strokeWidth = lineThicknessPx
        cornerPaint.style = Paint.Style.STROKE
        return cornerPaint
    }
}