package crop

interface Easing {
    fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double
    fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double
    fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double
}
