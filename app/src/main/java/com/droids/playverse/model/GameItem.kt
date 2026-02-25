package com.droids.playverse.model

import androidx.compose.ui.graphics.Color

data class GameItem(
    val id  : String,
    val title:String,
    val image:Int,
    val description: String,
    val color: Int,
    val playRoute:String,
    val info_image:Int,
    val info_banner:Int,
)