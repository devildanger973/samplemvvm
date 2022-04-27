package crop

/**
 *
 */
enum class RatioText(
    /**
     *
     */
    val aspectRatio: AspectRatio
) {

    /**
     *
     */
    FREE(AspectRatio()),

    /**
     *
     */
    FIT_IMAGE(AspectRatio(2.35, 1)),

    /**
     *
     */
    SQUARE(AspectRatio(1.0, 1)),

    /**
     *
     */
    RATIO_3_4(AspectRatio(3.0, 4)),

    /**
     *
     */
    RATIO_4_3(AspectRatio(4.0, 3)),

    /**
     *
     */
    RATIO_9_16(AspectRatio(9.0, 16)),

    /**
     *
     */
    RATIO_16_9(AspectRatio(16.0, 9)),

    /**
     *
     */
    RATIO_4_5(AspectRatio(4.0, 5)),

    /**
     *
     */
    RATIO_IG_story(AspectRatio(19.0, 40)),

    /**
     *
     */
    RATIO_3_2(AspectRatio(3.0, 2)),

    /**
     *
     */
    RATIO_1_2(AspectRatio(1.0, 2)),

    /**
     *
     */
    RATIO_Cover(AspectRatio(40.0, 19)),

    /**
     *
     */
    RATIO_2_3(AspectRatio(2.0, 3)),

    /**
     *
     */
    RATIO_2_1(AspectRatio(2.0, 1)),


}

/**
 *
 */
data class AspectRatio(
    /**
     *
     */
    val aspectX: Double = 0.0,
    /**
     *
     */
    val aspectY: Int = 0
)
