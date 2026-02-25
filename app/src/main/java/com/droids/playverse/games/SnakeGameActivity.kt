package com.droids.playverse.games

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droids.playverse.AdConstants.TEST_INTERSTITIAL_ID
import com.droids.playverse.InterstitialAdManager
import com.droids.playverse.SnakeGameScreen
import com.droids.playverse.SnakeGameViewModel
import com.droids.playverse.ui.theme.SnakeGameTheme

class SnakeGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InterstitialAdManager.load(
            this,
            TEST_INTERSTITIAL_ID
        )

        setContent {
            SnakeGameTheme {
                val viewModel = viewModel<SnakeGameViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                SnakeGameScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}