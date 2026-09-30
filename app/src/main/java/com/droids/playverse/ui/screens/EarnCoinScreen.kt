package com.droids.playverse.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.droids.playverse.ads.AdConstants
import com.droids.playverse.R
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.ads.RewardedAdManager
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.AppColors
import com.droids.playverse.ui.viewmodel.CoinViewModel
import kotlinx.coroutines.delay

private const val WATCH_AD_REWARD = 50

@Composable
fun EarnCoinsScreen(navController: NavController) {
    val activity = LocalActivity.current
    val coinViewModel: CoinViewModel = viewModel()
    val coinBalance by coinViewModel.balance.collectAsStateWithLifecycle()
    var statusMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        activity?.let {
            RewardedAdManager.load(it, AdConstants.TEST_REWARDED_ID)
        }
    }

    LaunchedEffect(statusMessage) {
        if (statusMessage != null) {
            delay(2500)
            statusMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
    ) {
        TopBar(
            title = "EARN COINS",
            onBack = { navController.popBackStack() }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(4.dp)
                .background(Color(0xFFFFC93C))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(AppColors.gradientColors2))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BalanceCoinGlyph()
            Spacer(Modifier.height(10.dp))
            Text(
                text = coinBalance.toString(),
                color = Color(0xFF3F4A7A),
                fontSize = 40.sp,
                fontFamily = FontFamily(Font(R.font.poppins_extrabold))
            )
            Text(
                text = "YOUR COINS",
                color = Color(0xFF8A93B8),
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular))
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = {
                    val currentActivity = activity
                    if (currentActivity == null) {
                        statusMessage = "Ad not ready — try again in a moment"
                        return@Button
                    }
                    SoundManager.playPop()
                    var earnedThisWatch = false
                    RewardedAdManager.show(
                        activity = currentActivity,
                        adUnitId = AdConstants.TEST_REWARDED_ID,
                        source = "earn_coins_watch_ad",
                        rewardAmount = WATCH_AD_REWARD,
                        onRewardEarned = { earnedThisWatch = true },
                        onDismiss = {
                            statusMessage = if (earnedThisWatch) {
                                "You earned $WATCH_AD_REWARD coins!"
                            } else {
                                "Ad not ready — try again in a moment"
                            }
                        }
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A9D5D)),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(64.dp)
            ) {
                Text(
                    text = "Watch Ad (+$WATCH_AD_REWARD 🪙)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_extrabold))
                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = statusMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = statusMessage.orEmpty(),
                    color = Color(0xFF3F4A7A),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(40.dp))

            Text(
                text = "Tip: you also pick up a few coins automatically\nevery time you finish a game.",
                color = Color(0xFF8A93B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.inter_regular))
            )
        }
    }
}

@Composable
private fun BalanceCoinGlyph() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFFFFE066), Color(0xFFFFB300))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "C",
                color = Color(0xFF8A5A00),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(2.dp))
    }
}