package com.example.myapplication

/**
 *
 */
data class HeroSelected(

    var imagePath: String? = null,
    /**
     *
     */
    val viewType: Int,
    /**
     *
     */
    var isSelected: Boolean = false,
    val image: Int = -1
)

