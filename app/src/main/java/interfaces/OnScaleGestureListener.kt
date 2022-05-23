package interfaces

import android.view.View

/**
 * The listener for receiving notifications when gestures occur.
 * If you want to listen for all the different gestures then implement
 * this interface. If you only want to listen for a subset it might
 * be easier to extend [ScaleGestureDetector.SimpleOnScaleGestureListener].
 *
 *
 * An application will receive events in the following order:
 */
interface OnScaleGestureListener {
    /**
     * Responds to scaling events for a gesture in progress.
     * Reported by pointer motion.
     *
     * @param detectorScale The detector reporting the event - use this to
     * retrieve extended info about event state.
     * @return Whether or not the detector should consider this event
     * as handled. If an event was not handled, the detector
     * will continue to accumulate movement until an event is
     * handled. This can be useful if an application, for example,
     * only wants to update scaling factors if the change is
     * greater than 0.01.
     */
    fun onScale(view: View?, detectorScale: ScaleGestureDetector?): Boolean

    /**
     * Responds to the beginning of a scaling gesture. Reported by
     * new pointers going down.
     *
     * @param detectorScale The detector reporting the event - use this to
     * retrieve extended info about event state.
     * @return Whether or not the detector should continue recognizing
     * this gesture. For example, if a gesture is beginning
     * with a focal point outside of a region where it makes
     * sense, onScaleBegin() may return false to ignore the
     * rest of the gesture.
     */
    fun onScaleBegin(view: View?, detectorScale: ScaleGestureDetector?): Boolean

    /**
     * Responds to the end of a scale gesture. Reported by existing
     * pointers going up.
     *
     *
     * Once a scale has ended, [ScaleGestureDetector.getFocusX]
     * and [ScaleGestureDetector.getFocusY] will return the location
     * of the pointer remaining on the screen.
     *
     * @param detectorScale The detector reporting the event - use this to
     * retrieve extended info about event state.
     */
    fun onScaleEnd(view: View?, detectorScale: ScaleGestureDetector?)
}
