package interfaces

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

/**
 * Detects transformation gestures involving more than one pointer ("multitouch")
 * using the supplied [MotionEvent]s. The [OnScaleGestureListener]
 * callback will notify users when a particular gesture event has occurred.
 * This class should only be used with [MotionEvent]s reported via touch.
 *
 *
 * To use this class:
 *
 *  * Create an instance of the `ScaleGestureDetector` for your
 * [View]
 *
 */
class ScaleGestureDetector internal constructor(private val listener: OnScaleGestureListener) {
    /**
     * Returns `true` if a two-finger scale gesture is in progress.
     *
     * @return `true` if a scale gesture is in progress, `false` otherwise.
     */
    var isInProgress = false
        private set
    private var prevEvent: MotionEvent? = null
    private var currEvent: MotionEvent? = null
    val currentSpanVector: Vector2D

    /**
     * Get the X coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is directly between
     * the two pointers forming the gesture.
     * If a gesture is ending, the focal point is the location of the
     * remaining pointer on the screen.
     * If [.isInProgress] would return false, the result of this
     * function is undefined.
     *
     * @return X coordinate of the focal point in pixels.
     */
    var focusX = 0f
        private set

    /**
     * Get the Y coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is directly between
     * the two pointers forming the gesture.
     * If a gesture is ending, the focal point is the location of the
     * remaining pointer on the screen.
     * If [.isInProgress] would return false, the result of this
     * function is undefined.
     *
     * @return Y coordinate of the focal point in pixels.
     */
    var focusY = 0f
        private set

    /**
     * Return the previous x distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    var previousSpanX = 0f
        private set

    /**
     * Return the previous y distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    var previousSpanY = 0f
        private set

    /**
     * Return the current x distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    var currentSpanX = 0f
        private set

    /**
     * Return the current y distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    var currentSpanY = 0f
        private set
    private var currLen = 0f
    private var prevLen = 0f
    private var scaleFactor = 0f
    private var currPressure = 0f
    private var prevPressure = 0f

    /**
     * Return the time difference in milliseconds between the previous
     * accepted scaling event and the current scaling event.
     *
     * @return Time difference since the last scaling event in milliseconds.
     */
    var timeDelta: Long = 0
        private set
    private var invalidGesture = false

    // Pointer IDs currently responsible for the two fingers controlling the gesture
    private var activeId0 = 0
    private var activeId1 = 0
    private var active0MostRecent = false
    fun onTouchEvent(view: View, event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            reset() // Start fresh
        }
        var handled = true
        if (invalidGesture) {
            handled = false
        } else if (!isInProgress) {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    activeId0 = event.getPointerId(0)
                    active0MostRecent = true
                }
                MotionEvent.ACTION_UP -> reset()
                MotionEvent.ACTION_POINTER_DOWN -> {

                    // We have a new multi-finger gesture
                    if (prevEvent != null) prevEvent!!.recycle()
                    prevEvent = MotionEvent.obtain(event)
                    timeDelta = 0
                    val index1 = event.actionIndex
                    var index0 = event.findPointerIndex(activeId0)
                    activeId1 = event.getPointerId(index1)
                    if (index0 < 0 || index0 == index1) {
                        // Probably someone sending us a broken event stream.
                        index0 = findNewActiveIndex(event, activeId1, -1)
                        activeId0 = event.getPointerId(index0)
                    }
                    active0MostRecent = false
                    setContext(view, event)
                    isInProgress = listener.onScaleBegin(view, this)
                }
            }
        } else {
            // Transform gesture in progress - attempt to handle it
            when (action) {
                MotionEvent.ACTION_POINTER_DOWN -> {

                    // End the old gesture and begin a new one with the most recent two fingers.
                    listener.onScaleEnd(view, this)
                    val oldActive0 = activeId0
                    val oldActive1 = activeId1
                    reset()
                    prevEvent = MotionEvent.obtain(event)
                    activeId0 = if (active0MostRecent) oldActive0 else oldActive1
                    activeId1 = event.getPointerId(event.actionIndex)
                    active0MostRecent = false
                    var index0 = event.findPointerIndex(activeId0)
                    if (index0 < 0 || activeId0 == activeId1) {
                        // Probably someone sending us a broken event stream.
                        index0 = findNewActiveIndex(event, activeId1, -1)
                        activeId0 = event.getPointerId(index0)
                    }
                    setContext(view, event)
                    isInProgress = listener.onScaleBegin(view, this)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerCount = event.pointerCount
                    val actionIndex = event.actionIndex
                    val actionId = event.getPointerId(actionIndex)
                    var gestureEnded = false
                    if (pointerCount > 2) {
                        if (actionId == activeId0) {
                            val newIndex = findNewActiveIndex(event, activeId1, actionIndex)
                            if (newIndex >= 0) {
                                listener.onScaleEnd(view, this)
                                activeId0 = event.getPointerId(newIndex)
                                active0MostRecent = true
                                prevEvent = MotionEvent.obtain(event)
                                setContext(view, event)
                                isInProgress = listener.onScaleBegin(view, this)
                            } else {
                                gestureEnded = true
                            }
                        } else if (actionId == activeId1) {
                            val newIndex = findNewActiveIndex(event, activeId0, actionIndex)
                            if (newIndex >= 0) {
                                listener.onScaleEnd(view, this)
                                activeId1 = event.getPointerId(newIndex)
                                active0MostRecent = false
                                prevEvent = MotionEvent.obtain(event)
                                setContext(view, event)
                                isInProgress = listener.onScaleBegin(view, this)
                            } else {
                                gestureEnded = true
                            }
                        }
                        prevEvent!!.recycle()
                        prevEvent = MotionEvent.obtain(event)
                        setContext(view, event)
                    } else {
                        gestureEnded = true
                    }
                    if (gestureEnded) {
                        // Gesture ended
                        setContext(view, event)

                        // Set focus point to the remaining finger
                        val activeId = if (actionId == activeId0) activeId1 else activeId0
                        val index = event.findPointerIndex(activeId)
                        focusX = event.getX(index)
                        focusY = event.getY(index)
                        listener.onScaleEnd(view, this)
                        reset()
                        activeId0 = activeId
                        active0MostRecent = true
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    listener.onScaleEnd(view, this)
                    reset()
                }
                MotionEvent.ACTION_UP -> reset()
                MotionEvent.ACTION_MOVE -> {
                    setContext(view, event)

                    // Only accept the event if our relative pressure is within
                    // a certain limit - this can help filter shaky data as a
                    // finger is lifted.
                    if (currPressure / prevPressure > PRESSURE_THRESHOLD) {
                        val updatePrevious = listener.onScale(view, this)
                        if (updatePrevious) {
                            prevEvent!!.recycle()
                            prevEvent = MotionEvent.obtain(event)
                        }
                    }
                }
            }
        }
        return handled
    }

    private fun findNewActiveIndex(
        ev: MotionEvent,
        otherActiveId: Int,
        removedPointerIndex: Int,
    ): Int {
        val pointerCount = ev.pointerCount

        // It's ok if this isn't found and returns -1, it simply won't match.
        val otherActiveIndex = ev.findPointerIndex(otherActiveId)

        // Pick a new id and update tracking state.
        for (i in 0 until pointerCount) {
            if (i != removedPointerIndex && i != otherActiveIndex) {
                return i
            }
        }
        return -1
    }

    private fun setContext(view: View, curr: MotionEvent) {
        if (currEvent != null) {
            currEvent!!.recycle()
        }
        currEvent = MotionEvent.obtain(curr)
        currLen = -1f
        prevLen = -1f
        scaleFactor = -1f
        currentSpanVector[0.0f] = 0.0f
        val prev = prevEvent
        val prevIndex0 = prev!!.findPointerIndex(activeId0)
        val prevIndex1 = prev.findPointerIndex(activeId1)
        val currIndex0 = curr.findPointerIndex(activeId0)
        val currIndex1 = curr.findPointerIndex(activeId1)
        if (prevIndex0 < 0 || prevIndex1 < 0 || currIndex0 < 0 || currIndex1 < 0) {
            invalidGesture = true
            if (isInProgress) {
                listener.onScaleEnd(view, this)
            }
            return
        }
        val px0 = prev.getX(prevIndex0)
        val py0 = prev.getY(prevIndex0)
        val px1 = prev.getX(prevIndex1)
        val py1 = prev.getY(prevIndex1)
        val cx0 = curr.getX(currIndex0)
        val cy0 = curr.getY(currIndex0)
        val cx1 = curr.getX(currIndex1)
        val cy1 = curr.getY(currIndex1)
        val pvx = px1 - px0
        val pvy = py1 - py0
        val cvx = cx1 - cx0
        val cvy = cy1 - cy0
        currentSpanVector[cvx] = cvy
        previousSpanX = pvx
        previousSpanY = pvy
        currentSpanX = cvx
        currentSpanY = cvy
        focusX = cx0 + cvx * 0.5f
        focusY = cy0 + cvy * 0.5f
        timeDelta = curr.eventTime - prev.eventTime
        currPressure = curr.getPressure(currIndex0) + curr.getPressure(currIndex1)
        prevPressure = prev.getPressure(prevIndex0) + prev.getPressure(prevIndex1)
    }

    private fun reset() {
        if (prevEvent != null) {
            prevEvent!!.recycle()
            prevEvent = null
        }
        if (currEvent != null) {
            currEvent!!.recycle()
            currEvent = null
        }
        isInProgress = false
        activeId0 = -1
        activeId1 = -1
        invalidGesture = false
    }

    /**
     * Return the current distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    private val currentSpan: Float
        private get() {
            if (currLen == -1f) {
                val cvx = currentSpanX
                val cvy = currentSpanY
                currLen = Math.sqrt((cvx * cvx + cvy * cvy).toDouble()).toFloat()
            }
            return currLen
        }

    /**
     * Return the previous distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    private val previousSpan: Float
        private get() {
            if (prevLen == -1f) {
                val pvx = previousSpanX
                val pvy = previousSpanY
                prevLen = Math.sqrt((pvx * pvx + pvy * pvy).toDouble()).toFloat()
            }
            return prevLen
        }

    /**
     * Return the scaling factor from the previous scale event to the current
     * event. This value is defined as
     * ([.getCurrentSpan] / [.getPreviousSpan]).
     *
     * @return The current scaling factor.
     */
    fun getScaleFactor(): Float {
        if (scaleFactor == -1f) {
            scaleFactor = currentSpan / previousSpan
        }
        return scaleFactor
    }

    /**
     * Return the event time of the current event being processed.
     *
     * @return Current event time in milliseconds.
     */
    val eventTime: Long
        get() = currEvent!!.eventTime

    companion object {
        /**
         * This value is the threshold ratio between our previous combined pressure
         * and the current combined pressure. We will only fire an onScale event if
         * the computed ratio between the current and previous event pressures is
         * greater than this value. When pressure decreases rapidly between events
         * the position values can often be imprecise, as it usually indicates
         * that the user is in the process of lifting a pointer off of the device.
         * Its value was tuned experimentally.
         */
        private const val PRESSURE_THRESHOLD = 0.67f
    }

    init {
        currentSpanVector = Vector2D()
    }
}