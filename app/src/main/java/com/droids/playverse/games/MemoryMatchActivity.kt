package com.droids.playverse.games

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droids.playverse.ads.AdConstants.TEST_BANNER_ID
import com.droids.playverse.ads.AdConstants.TEST_INTERSTITIAL_ID
import com.droids.playverse.ads.InterstitialAdManager
import com.droids.playverse.R
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.data.ServiceLocator
import com.droids.playverse.ui.screens.requestReview
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.BgBottom
import com.droids.playverse.ui.theme.BgTop
import com.droids.playverse.ui.theme.BoardColor
import com.droids.playverse.ui.theme.OColor
import com.droids.playverse.ui.theme.XColor
import kotlinx.coroutines.delay

@Preview(showSystemUi = true)
@Composable
fun MemoryMatchScreen(onExit: () -> Unit = {}) {
    // 11 unique pairs = 22 cards total
    var imageResources = remember {
        val base = listOf(
            R.drawable.memory_star, R.drawable.memory_cookie, R.drawable.memory_bulb,
            R.drawable.memory_love, R.drawable.memory_toy, R.drawable.memory_gift,
            R.drawable.memory_ladybug, R.drawable.memory_icecream,R.drawable.memory_leaf,
            R.drawable.memory_rocket, R.drawable.memory_unicorn
        )
        (base + base).shuffled()
    }

    var revealedIndices by remember { mutableStateOf(setOf<Int>()) }
    var matchedIndices by remember { mutableStateOf(setOf<Int>()) }
    var isBlueTurn by remember { mutableStateOf(true) }
    var blueScore by remember { mutableIntStateOf(0) }
    var redScore by remember { mutableIntStateOf(0) }
    var context= LocalContext.current
    val activity = context as Activity
    var winner by remember { mutableStateOf<String?>(null) }
    var hasShownExitAd by remember { mutableStateOf(false) }

    // "Score" is the winning player's pair count for a single round — higher is better.
    val scoreRepository = remember { ServiceLocator.provideScoreRepository() }
    val bestScore by scoreRepository.highScoreFlow("memory_match").collectAsStateWithLifecycle(initialValue = 0)

    BackHandler {
        if (!hasShownExitAd) {
            hasShownExitAd = true
            InterstitialAdManager.show(activity) {
                onExit()
            }
        } else {
            onExit()
        }
    }

    LaunchedEffect(Unit) {
        InterstitialAdManager.load(activity, TEST_INTERSTITIAL_ID)
    }


    // Matching Logic
    LaunchedEffect(revealedIndices) {
        val active = revealedIndices - matchedIndices
        if (active.size == 2) {
            val list = active.toList()
            delay(1000)
            if (imageResources[list[0]] == imageResources[list[1]]) {
                matchedIndices = matchedIndices + list[0] + list[1]
                if (isBlueTurn) blueScore++ else redScore++
                SoundManager.playPointSound()
            } else {
                isBlueTurn = !isBlueTurn
                SoundManager.playNoPointSound()
            }
            revealedIndices = matchedIndices

            if (matchedIndices.size == imageResources.size) {
                winner = if (blueScore > redScore) "BLUE" else if (redScore > blueScore) "RED" else "DRAW"
                ServiceLocator.provideCoinRepository().earnFromSession("memory_match")
                ServiceLocator.provideCoinRepository().earnFromScore("memory_match", blueScore + redScore)
                val isNewBest = scoreRepository.submitScore("memory_match", maxOf(blueScore, redScore))
                if (isNewBest) requestReview(context)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFE0D7FF), Color(0xFFF3EEFF))))) {
        TopBar("MEMORY MATCH", Color(0xFF6A63C5), onBack = onExit)
        Spacer(Modifier.height(12.dp))
        Spacer(Modifier.height(2.dp).fillMaxWidth().background(Color.Black))


        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Brush.verticalGradient(listOf(BgTop, BoardColor, BgBottom)))) {

            // 3-4-4-4-4-3 Pattern Logic
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 20.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rowPattern = listOf(3, 4, 4, 4, 4, 3)
                var currentIndex = 0

                rowPattern.forEach { count ->
                    Row(horizontalArrangement = Arrangement.spacedBy(23.dp)) {
                        repeat(count) {
                            val index = currentIndex
                            MemoryCard(
                                drawableRes = imageResources[index],
                                isRevealed = revealedIndices.contains(index),
                                isMatched = matchedIndices.contains(index),
                                onClick = {
                                    SoundManager.playPop()
                                    if (revealedIndices.size - matchedIndices.size < 2 && index !in revealedIndices) {
                                        revealedIndices = revealedIndices + index
                                        // SoundManager.playBottleSound()
                                    }
                                }
                            )
                            currentIndex++
                        }
                    }
                }
            }

            if(winner!=null) {
                Column(modifier = Modifier.background(Color.Black.copy(0.7f))) {
                    GameOverScreen4(
                        winner = winner!!,
                        bestScore = bestScore,
                        onRestart = {
                            InterstitialAdManager.show(activity) {
                                // Reset all states
                                revealedIndices = emptySet()
                                matchedIndices = emptySet()
                                blueScore = 0
                                redScore = 0
                                isBlueTurn = true
                                winner = null
                                // Re-shuffle images for new game
                                val base = listOf(
                                    R.drawable.memory_star,
                                    R.drawable.memory_cookie,
                                    R.drawable.memory_bulb,
                                    R.drawable.memory_love,
                                    R.drawable.memory_toy,
                                    R.drawable.memory_gift,
                                    R.drawable.memory_ladybug,
                                    R.drawable.memory_icecream,
                                    R.drawable.memory_leaf,
                                    R.drawable.memory_rocket,
                                    R.drawable.memory_unicorn
                                )
                                imageResources = (base + base).shuffled()
                            }
                        }
                    )
                }
            }

            // Winner Banner

        }
        Column {
            Spacer(Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        .offset(y = -20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreBox2(
                        score = blueScore,
                        color = OColor,
                        modifier = if (isBlueTurn) {
                            Modifier.border(3.dp, OColor, RoundedCornerShape(12.dp))
                        } else {
                            Modifier // No border when not their turn
                        }
                    )

                    // Red Score Box
                    ScoreBox2(
                        score = redScore,
                        color = XColor,
                        modifier = if (!isBlueTurn) {
                            Modifier.border(3.dp, XColor, RoundedCornerShape(12.dp))
                        } else {
                            Modifier // No border when not their turn
                        }
                    )
                }

            }
            BannerAdView(adUnitId = TEST_BANNER_ID)
            Spacer(modifier = Modifier.height(15.dp))
        }

        // Bottom UI with Turn Indicator Border
    }
}

@Composable
fun MemoryCard(drawableRes: Int, isRevealed: Boolean, isMatched: Boolean, onClick: () -> Unit) {
    // Rotation for unfold animation
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed || isMatched) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )

    // Alpha for blurring/fading out matched cards
    val matchedAlpha by animateFloatAsState(
        targetValue = if (isMatched) 0.3f else 1f,
        animationSpec = tween(800)
    )

    Box(
        modifier = Modifier
            .size(56.dp) // Adjusted size to match your image precisely
            .alpha(matchedAlpha)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 15f * density
            }
            .clickable(enabled = !isRevealed && !isMatched) { onClick() }
            .background(
                if (rotation <= 90f) Color(0xFFFFD600) else Color.White,
                RoundedCornerShape(8.dp)
            )
            .border(3.dp,Color.White,shape=RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (rotation > 90f) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (drawableRes == R.drawable.memory_leaf) 1.25f else 1f)
                    .padding(2.dp) // Small padding to keep card border visible
                    .graphicsLayer { rotationY = 180f },
                contentScale = ContentScale.Fit // High quality scaling to avoid blurring
            )
        }
    }
}
@Composable
fun ScoreBox2(score: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        // The modifier passed from the screen is applied here
        modifier = modifier
            .width(80.dp)
            .height(45.dp)
            .background(Color(0xFF2D2D39), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score.toString(),
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun GameOverScreen4(
    winner:String,
    bestScore: Int = 0,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "GAME OVER !",
            fontFamily = FontFamily(Font(R.font.poppins_bold)),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(75.dp)
                .clip(RoundedCornerShape(30.dp))
        ) {

            // 🌫 Blurred background
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .blur(20.dp)
            )


            // 📝 Sharp text on top
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("$winner WINS!", color = if(winner == "BLUE") OColor else XColor, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Best round score: $bestScore", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
            shape = RoundedCornerShape(50)
        ) {
            Text("Play Again", fontSize = 18.sp)

        }

    }
}