package com.droids.playverse.games.snakeGame

import androidx.compose.ui.geometry.Offset

sealed class SnakeGameEvent {
    data object StartGame : SnakeGameEvent()
    data object PauseGame : SnakeGameEvent()
    data object ResetGame : SnakeGameEvent()
    data object ContinueGame : SnakeGameEvent()
    data class UpdateDirection(val offset: Offset, val canvasWidth: Int) : SnakeGameEvent()
}