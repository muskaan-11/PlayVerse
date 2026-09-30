package com.droids.playverse.games.snakeGame

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droids.playverse.ads.AdConstants.TEST_INTERSTITIAL_ID
import com.droids.playverse.ads.AdConstants.TEST_REWARDED_ID
import com.droids.playverse.ads.InterstitialAdManager
import com.droids.playverse.ads.RewardedAdManager
import com.droids.playverse.ui.theme.SnakeGameTheme

@Composable
fun SnakeGame(onExit: () -> Unit = {}) {
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        activity?.let {
            InterstitialAdManager.load(it, TEST_INTERSTITIAL_ID)
            RewardedAdManager.load(it, TEST_REWARDED_ID)
        }
    }

    SnakeGameTheme {
        val viewModel = viewModel<SnakeGameViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        SnakeGameScreen(
            state = state,
            onEvent = viewModel::onEvent,
            onExit = onExit
        )
    }
}