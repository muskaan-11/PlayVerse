package com.droids.playverse.games

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droids.playverse.ads.AdConstants
import com.droids.playverse.ui.components.ContinueConfig
import com.droids.playverse.ads.InterstitialAdManager
import com.droids.playverse.R
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.ads.RewardedAdManager
import com.droids.playverse.data.ServiceLocator
import com.droids.playverse.ui.screens.requestReview
import com.droids.playverse.ui.components.ContinueDialog
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.viewmodel.CoinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class GameState {
    PLAYING,
    GAME_OVER
}
@Preview(showSystemUi = true)
@Composable
fun BrickBreakerScreen(onExit: () -> Unit = {}) {
    var hasShownInterstitial by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    var score by remember { mutableStateOf(0) }
    var context = LocalContext.current
    val scoreRepository = remember { ServiceLocator.provideScoreRepository() }
    val persistedBest by scoreRepository.highScoreFlow("brick").collectAsStateWithLifecycle(initialValue = 0)
    // Live "best" reflects the higher of what's on disk and the current run's score,
    // so the chip updates instantly during play without hitting the DB on every hit.
    val best = maxOf(persistedBest, score)
    var scoreSubmitted by remember { mutableStateOf(false) }
    var gameState by remember { mutableStateOf(GameState.PLAYING) }
    var resetTrigger by remember { mutableIntStateOf(0) }
    var continueTrigger by remember { mutableIntStateOf(0) }
    val coinViewModel: CoinViewModel = viewModel()
    val coinBalance by coinViewModel.balance.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var continueOffered by remember { mutableStateOf(false) }
    var showContinueDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        activity?.let {
            InterstitialAdManager.load(it, AdConstants.TEST_INTERSTITIAL_ID)
            RewardedAdManager.load(it, AdConstants.TEST_REWARDED_ID)
        }
    }

    val handleGameOver = {
        gameState = GameState.GAME_OVER
        coroutineScope.launch {
            ServiceLocator.provideCoinRepository().earnFromSession("brick")
            ServiceLocator.provideCoinRepository().earnFromScore("brick", score)
            if (!scoreSubmitted) {
                scoreSubmitted = true
                if (scoreRepository.submitScore("brick", score)) requestReview(context)
            }
        }
        if (!continueOffered) {
            showContinueDialog = true
        }
    }
    BackHandler {
        gameState=GameState.GAME_OVER
        if (!scoreSubmitted) {
            scoreSubmitted = true
            coroutineScope.launch {
                if (scoreRepository.submitScore("brick", score)) requestReview(context)
            }
        }
        if (!hasShownInterstitial) {
            hasShownInterstitial = true

            activity?.let {
                InterstitialAdManager.show(it) {
                    onExit()
                }
            } ?: onExit()

        } else {
            onExit()
        }
    }

    // 1. Wrap EVERYTHING in a Box to allow layering
    Box(modifier = Modifier.fillMaxSize(),) {

        // 2. The Main Game UI — always mounted so mid-run state (bricks, score,
        // paddle position) survives a continue instead of being torn down.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD1E9FF))
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.01f))
            TopBar("BRICK BREAKER", Color(0xFF2F79C9), onBack = onExit)
            Spacer(Modifier.height(12.dp))
            DividerLine2()

            Spacer(modifier = Modifier.fillMaxHeight(0.02f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GameScoreChip("SCORE: $score")
                GameScoreChip("BEST: $best")
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.89f).padding(10.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.brick_break_bg),
                    contentDescription = "img",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    GameBoard(
                        gameState = gameState,
                        resetTrigger = resetTrigger,
                        continueTrigger = continueTrigger,
                        width = constraints.maxWidth.toFloat(),
                        height = constraints.maxHeight.toFloat(),
                        onGameOver = handleGameOver,
                        onBrickHit = {
                            SoundManager.playBrickPointSound()
                            score++
                        },
                        onReset = { score = 0 }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            DividerLine2()
            Spacer(Modifier.height(10.dp))
            BannerAdView(
                adUnitId = AdConstants.TEST_BANNER_ID
            )
        }

        if (gameState == GameState.GAME_OVER) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .blur(16.dp)
            )
        }

        if (showContinueDialog) {
            ContinueDialog(
                cost = ContinueConfig.COST,
                coinBalance = coinBalance,
                onContinue = {
                    coroutineScope.launch {
                        if (ServiceLocator.provideCoinRepository().spend(ContinueConfig.COST, "continue_brick_breaker")) {
                            continueOffered = true
                            showContinueDialog = false
                            continueTrigger++
                            scoreSubmitted = false
                            gameState = GameState.PLAYING
                        }
                    }
                },
                onWatchAd = {
                    activity?.let {
                        var rewardEarned = false
                        RewardedAdManager.show(
                            activity = it,
                            adUnitId = AdConstants.TEST_REWARDED_ID,
                            source = "continue_brick_breaker_ad",
                            onRewardEarned = { rewardEarned = true },
                            onDismiss = {
                                continueOffered = true
                                showContinueDialog = false
                                if (rewardEarned) {
                                    continueTrigger++
                                    scoreSubmitted = false
                                    gameState = GameState.PLAYING
                                }
                            }
                        )
                    } ?: run {
                        continueOffered = true
                        showContinueDialog = false
                    }
                },
                onDecline = {
                    continueOffered = true
                    showContinueDialog = false
                }
            )
        } else if (gameState == GameState.GAME_OVER) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                Image(
                    painter = painterResource(id = R.drawable.brick_end_bg),
                    contentDescription = "img",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                GameOverScreen2(
                    score = score,
                    highScore = best,
                    onPlayAgain = {
                        val startFresh = {
                            score = 0
                            continueOffered = false
                            scoreSubmitted = false
                            resetTrigger++
                            gameState = GameState.PLAYING
                        }

                        if (!hasShownInterstitial) {
                            hasShownInterstitial = true

                            activity?.let {
                                InterstitialAdManager.show(it) { startFresh() }
                            } ?: startFresh()

                        } else {
                            startFresh()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GameBoard(gameState: GameState, resetTrigger: Int, continueTrigger: Int, width: Float, height: Float,onGameOver: () -> Unit, onBrickHit: () -> Unit, onReset: () -> Unit) {
    val brickCols = 7
    val bHeight = 50f
    val gutter = 4f
    val bWidth = (width - (gutter * (brickCols + 1))) / brickCols
    val paddleWidth = 220f
    val paddleHeight = 45f
    val ballRadius = 29f
    val bricksToHitBeforeNewRow = 10
    val minBricksOnScreen = 20

    // --- Speed Tuning ---
    val initialSpeed = 10f
    val maxSpeed = 28f
    val speedIncrement = 0.4f


    var paddleX by remember { mutableStateOf(width / 2 - paddleWidth / 2) }
    var ballPos by remember { mutableStateOf(Offset(width / 2, height * 0.7f)) }

    var currentSpeed by remember { mutableStateOf(initialSpeed) }
    var ballVelocity by remember { mutableStateOf(Offset(initialSpeed, -initialSpeed)) }
    var hitCounter by remember { mutableStateOf(0) }
    var lastResetTrigger by remember { mutableStateOf(-1) }
    var lastContinueTrigger by remember { mutableStateOf(continueTrigger) }

    val bricks = remember { mutableStateListOf<BrickData>() }

    fun addNewRow() {
        val randomColorIndex = (0..4).random()
        val newRowColor = getRowColor(randomColorIndex)
        val shiftAmount = bHeight + gutter
        for (i in bricks.indices) {
            bricks[i] = bricks[i].copy(rect = bricks[i].rect.translate(0f, shiftAmount))
        }
        repeat(brickCols) { col ->
            bricks.add(BrickData(
                rect = Rect(col * (bWidth + gutter) + gutter, 20f, col * (bWidth + gutter) + gutter + bWidth, 20f + bHeight),
                color = newRowColor
            ))
        }
    }

    LaunchedEffect(gameState, resetTrigger, continueTrigger) {
        if (gameState == GameState.PLAYING) {
            if (resetTrigger != lastResetTrigger) {
                // Fresh run: rebuild the brick layout and reset everything.
                bricks.clear()
                repeat(5) { row ->
                    repeat(brickCols) { col ->
                        bricks.add(BrickData(
                            rect = Rect(col * (bWidth + gutter) + gutter, row * (bHeight + gutter) + 20f, col * (bWidth + gutter) + gutter + bWidth, row * (bHeight + gutter) + 20f + bHeight),
                            color = getRowColor(row)
                        ))
                    }
                }

                currentSpeed = initialSpeed
                ballVelocity = Offset(initialSpeed, -initialSpeed)
                ballPos = Offset(width / 2, height * 0.7f)
                paddleX = width / 2 - paddleWidth / 2
                hitCounter = 0
                lastResetTrigger = resetTrigger
                lastContinueTrigger = continueTrigger
            } else if (continueTrigger != lastContinueTrigger) {
                // Continue: keep the brick layout, score and paddle position —
                // just re-serve the ball so the player isn't insta-killed by the
                // same drop that ended the previous run.
                ballVelocity = Offset(currentSpeed, -currentSpeed)
                ballPos = Offset(paddleX + paddleWidth / 2, height * 0.7f)
                bricks.removeAll { it.rect.bottom >= height - 150f }
                lastContinueTrigger = continueTrigger
            }

            while (gameState == GameState.PLAYING) {
                var nextX = ballPos.x + ballVelocity.x
                var nextY = ballPos.y + ballVelocity.y

                // --- 1. WALL COLLISIONS (WITH SNAPPING) ---
                if (nextX <= ballRadius) {
                    SoundManager.playBrickHitSound()
                    ballVelocity = ballVelocity.copy(x = Math.abs(ballVelocity.x))
                    nextX = ballRadius // Snap to inner edge
                } else if (nextX >= width - ballRadius) {
                    SoundManager.playBrickHitSound()
                    ballVelocity = ballVelocity.copy(x = -Math.abs(ballVelocity.x))
                    nextX = width - ballRadius // Snap to inner edge
                }

                if (nextY <= ballRadius) {
                    SoundManager.playBrickHitSound()
                    ballVelocity = ballVelocity.copy(y = Math.abs(ballVelocity.y)) // Force move Down
                    nextY = ballRadius + 1f // Snap below ceiling
                }

                // --- 2. PADDLE COLLISION ---
                val paddleRect = Rect(paddleX, height - 120f, paddleX + paddleWidth, height - 120f + paddleHeight)
                if (nextY + ballRadius >= paddleRect.top &&
                    nextX >= paddleRect.left && nextX <= paddleRect.right &&
                    ballVelocity.y > 0) {
                    SoundManager.playBrickHitSound()

                    ballVelocity = ballVelocity.copy(y = -Math.abs(ballVelocity.y)) // Force move Up
                    nextY = paddleRect.top - ballRadius // Snap above paddle
                }

                // --- 3. BRICK COLLISION ---
                val iterator = bricks.listIterator()
                while (iterator.hasNext()) {
                    val brick = iterator.next()
                    if (brick.rect.contains(Offset(nextX, nextY))) {
                        iterator.remove()

                        // Update speed magnitude
                        currentSpeed = (currentSpeed + speedIncrement).coerceAtMost(maxSpeed)
                        val signX = if (ballVelocity.x > 0) 1f else -1f

                        // Determine bounce direction based on hit position
                        if (nextY < brick.rect.top + 10f) {
                            ballVelocity = Offset(currentSpeed * signX, -Math.abs(currentSpeed))
                        } else {
                            ballVelocity = Offset(currentSpeed * signX, Math.abs(currentSpeed))
                        }

                        onBrickHit()
                        hitCounter++

                        if (hitCounter >= bricksToHitBeforeNewRow || bricks.size < minBricksOnScreen) {

                            while (bricks.size < minBricksOnScreen) {
                                addNewRow()
                            }

                            hitCounter = 0
                        }

                        break
                    }
                }

                // --- 4. GAME OVER CONDITIONS ---
                if (nextY > height || bricks.any { it.rect.bottom >= height - 150f }) {
                    SoundManager.playHitSound()
                    onGameOver()
                }

                ballPos = Offset(nextX, nextY)
                delay(16L)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        paddleX = (paddleX + drag.x).coerceIn(0f, width - paddleWidth)
                    }
                }
        ) {
            bricks.forEach { brick ->
                drawRoundRect(
                    color = brick.color,
                    topLeft = brick.rect.topLeft,
                    size = brick.rect.size,
                    cornerRadius = CornerRadius(4f)
                )
            }

            // Ball
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFE3F2FD), Color(0xFF90CAF9), Color(0xFF4D8CFF)),
                    center = ballPos - Offset(ballRadius * 0.3f, ballRadius * 0.3f),
                    radius = ballRadius * 1.4f
                ),
                radius = ballRadius,
                center = ballPos
            )

            // Paddle
            drawRoundRect(
                color = Color.Red,
                topLeft = Offset(paddleX, size.height - 120f),
                size = Size(paddleWidth, paddleHeight),
                cornerRadius = CornerRadius(50f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(paddleX + 40f, size.height - 120f),
                size = Size(paddleWidth - 80f, paddleHeight),
            )
        }


    }

}
data class BrickData(val rect: Rect, val color: Color)

fun getRowColor(row: Int): Color = when (row % 5) {
    0 -> Color(0xFFFF5252) // Red
    1 -> Color(0xFFFFB74D) // Orange
    2 -> Color(0xFFFFF176) // Yellow
    3 -> Color(0xFF81C784) // Green
    else -> Color(0xFF4FC3F7) // Blue
}

@Composable
fun GameScoreChip(text: String) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(35.dp)
            .background(brush = Brush.linearGradient(listOf(Color(0xFF4D8CFF),
                Color(0xFF2F6BFF))), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color(0xFFFFD84D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

//@Composable
//fun GameOverUI(onRetry: () -> Unit) {
//    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.6f)), contentAlignment = Alignment.Center) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text("GAME OVER", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black)
//            Button(onClick = onRetry) { Text("RETRY") }
//        }
//    }
//}
@Composable
fun GameOverScreen2(
    score: Int,
    highScore: Int,
    onPlayAgain: () -> Unit
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
            color = Color(0xFFF44336),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
        )
        Image(
            painter = painterResource(R.drawable.brick_over_bg),
            contentDescription = "snake_end",
            modifier = Modifier.size(300.dp)
        )
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
                Text(
                    text ="The ball hit the ground !",
                    color = Color.White,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_bold))
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF5F99C9),
                            Color(0xFF2F7FB2)
                        ))
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // SCORE
                    Row {
                        Text(
                            text = "SCORE: ",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$score",
                            color = Color(0xFFFFF200),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // HIGH SCORE
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            painter = painterResource(R.drawable.trophy),
                            contentDescription = "trophy",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "HIGH SCORE: ",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )

                        Text(
                            text = "$highScore",
                            color = Color(0xFFFFF200),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        GameButton(
            text = "Play Again",
            enabled = true,
            150.dp,
            onClick = onPlayAgain
        )

    }
}
@Composable
private fun GameButton(
    text: String,
    enabled: Boolean,
    width : Dp,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .height(50.dp)
            .width(width)
            .clip(RoundedCornerShape(25.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF00BAFE),
                        Color(0xFF006D95)
                    )
                )
            )
            .clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_regular))
        )
    }
}
@Composable
fun DividerLine2(){
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(Color.Black)
    )
}