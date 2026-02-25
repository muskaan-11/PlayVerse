package com.droids.playverse.data

import androidx.compose.ui.graphics.Color
import com.droids.playverse.games.EntityType

data class Confetti( val startX: Float,
                     val endX: Float,
                     val size: Float,
                     val speed: Float,
                     val color: Color
)

