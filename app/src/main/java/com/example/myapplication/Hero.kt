package com.example.myapplication

/**
 *
 */
data class Hero(
    /**
     *
     */
    var name: String,
    /**
     *
     */
    var image: Int = -1,
    /**
     *
     */
    var imagePath: String? = null,
    /**
     *
     */
    val viewType: Int,
    /**
     *
     */
    var isSelected: Boolean = false,

    var isEdited: Boolean = false

)

