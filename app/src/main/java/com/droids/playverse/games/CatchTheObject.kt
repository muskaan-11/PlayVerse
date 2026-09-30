package com.droids.playverse.games

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.roundToInt
import kotlin.random.Random

/* ------------------ DATA ------------------ */

enum class ObjectType(val drawableRes: Int, val isBomb: Boolean,val sizeMultiplier: Float = 1f) {
    APPLE(R.drawable.ic_apple, false),
    BANANA(R.drawable.ic_banana, false),
    STRAWBERRY(R.drawable.ic_strawberry, false),
    CANDY(R.drawable.ic_candy, false),
    DIAMOND(R.drawable.ic_gem, false),
    BASKETBALL(R.drawable.ic_basketball, false),
    BOMB(R.drawable.ic_bomb, true,1.4f)
}

data class FallingObject(
    val id: Long = Random.nextLong(),
    var lane: Int,
    var x: Float,
    var y: Float,
    val type: ObjectType,
    var isCaught: Boolean = false, // Becomes true ONLY if it enters the top
    var isIgnored: Boolean = false  // Becomes true if it passes the rim outside the basket
)

/* ------------------ GAME ------------------ */
@Preview(showSystemUi = true)
@Composable
fun CatchTheObjectGame(onExit: () -> Unit = {}) {
    var hasShownInterstitial by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    val context = LocalContext.current

    val scoreRepository = remember { ServiceLocator.provideScoreRepository() }
    var score by remember { mutableIntStateOf(0) }
    val highScore by scoreRepository.highScoreFlow("catch").collectAsStateWithLifecycle(initialValue = 0)
    var gameOver by remember { mutableStateOf(false) }
    var bombHit by remember { mutableStateOf(false) }
    var shakeX by remember { mutableFloatStateOf(0f) }

    val scope = rememberCoroutineScope()
    val basketScale = remember { Animatable(1f) }
    var objects by remember { mutableStateOf(listOf<FallingObject>()) }
    val itemTypes = remember { ObjectType.values() }

    val coinViewModel: CoinViewModel = viewModel()
    val coinBalance by coinViewModel.balance.collectAsStateWithLifecycle()
    var continueOffered by remember { mutableStateOf(false) }
    var showContinueDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        activity?.let {
            InterstitialAdManager.load(it, AdConstants.TEST_INTERSTITIAL_ID)
            RewardedAdManager.load(it, AdConstants.TEST_REWARDED_ID)
        }
    }

    BackHandler {
        gameOver = true

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

    Box(modifier = Modifier.fillMaxSize().offset { IntOffset(shakeX.roundToInt(), 0) }) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFD6EFFF))) {
            TopBar("CATCH THE OBJECT", Color(0xFF2F79C9), onBack = onExit)
            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.height(2.dp).fillMaxWidth().background(Color.Black))

            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
                val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
                val sidePadding = with(LocalDensity.current) { 30.dp.toPx() }
                val playableWidth = widthPx - (sidePadding * 2)

                val basketSize = 120.dp
                val basketWidthPx = with(LocalDensity.current) { basketSize.toPx() }
                val objectSizePx = with(LocalDensity.current) { 75.dp.toPx() }
                val basketRimY = heightPx - 400f

                var basketX by remember { mutableFloatStateOf(widthPx / 2 - basketWidthPx / 2) }

                Image(painter = painterResource(R.drawable.bg_clouds), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

                // 1. Render Objects
                objects.forEach { obj ->
                    Image(
                        painter = painterResource(obj.type.drawableRes),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp * obj.type.sizeMultiplier).graphicsLayer {
                            translationX = obj.x
                            translationY = obj.y
                        }
                    )
                }

                // 2. Render Basket
                Image(
                    painter = painterResource(R.drawable.ic_basket),
                    contentDescription = "Basket",
                    modifier = Modifier.graphicsLayer { translationX = basketX; translationY = basketRimY }.size(basketSize).graphicsLayer(scaleX = basketScale.value, scaleY = basketScale.value)
                        .pointerInput(gameOver) {
                            if (!gameOver) {
                                detectDragGestures { change, drag ->
                                    change.consume()
                                    basketX = (basketX + drag.x).coerceIn(0f, widthPx - basketWidthPx)
                                }
                            }
                        }
                )

                /* -------- STAIRCASE ENGINE (FAIR PLAY) -------- */
                LaunchedEffect(gameOver) {
                    if (gameOver) return@LaunchedEffect

                    while (!gameOver) {
                        val currentSpeed = when {
                            score < 10 -> 14f
                            score < 25 -> 16f
                            score < 35 -> 18f
                            score < 45-> 22f
                            score < 50 -> 24f
                            score< 70->26f
                            score < 100 -> 28f
                            else -> 34f
                        }
                        val nextObjects = objects.map { it.copy(y = it.y + currentSpeed) }.toMutableList()

                        // --- THE STAIRCASE SPAWN ---
                        // Only spawn if the screen isn't crowded AND the last object has fallen 300px
                        val lastObjY = nextObjects.lastOrNull()?.y ?: 999f

                        if (nextObjects.size < 6 && lastObjY > 300f) {
                            val lastLane = nextObjects.lastOrNull()?.lane ?: -1
                            val laneIdx = (0 until 5).filter { it != lastLane }.random()

                            val laneWidth = playableWidth / 5
                            val xPos = sidePadding + (laneIdx * laneWidth) + (laneWidth - objectSizePx) / 2

                            // 25% Bomb Chance / 75% Fruit Chance
                            val isBomb = Random.nextFloat() < 0.25f

                            nextObjects.add(FallingObject(
                                lane = laneIdx, x = xPos, y = -150f,
                                type = if (isBomb) ObjectType.BOMB else itemTypes.filter { !it.isBomb }.random()
                            ))
                        }

                        // --- PHYSICS & COLLISION ---
                        val iterator = nextObjects.iterator()
                        while (iterator.hasNext()) {
                            val obj = iterator.next()
                            val mouthLeft = basketX + (basketWidthPx * 0.2f)
                            val mouthRight = basketX + (basketWidthPx * 0.8f)
                            val objCenterX = obj.x + (objectSizePx * obj.type.sizeMultiplier) / 2

                            // Top-Entry Check (Collision Gate)
                            if (!obj.isCaught && !obj.isIgnored) {
                                if (obj.y >= basketRimY && obj.y <= basketRimY + currentSpeed) {
                                    if (objCenterX in mouthLeft..mouthRight) {
                                        obj.isCaught = true
                                        if (obj.type.isBomb) {
                                            SoundManager.playBombSound()
                                            bombHit = true; gameOver = true;
                                            if (!continueOffered) {
                                                showContinueDialog = true
                                            }
                                            break
                                        }
                                        else{
                                            SoundManager.playCatchSound()
                                        }
                                    } else { obj.isIgnored = true }
                                }
                            }

                            // Caught vs Missed
                            if (obj.isCaught && obj.y > basketRimY + 80f) {
                                score += 1
                                iterator.remove()
                                scope.launch {
                                    basketScale.animateTo(1.1f, tween(80))
                                    basketScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy))
                                }
                            } else if (obj.y > heightPx) {
                                iterator.remove()
                            }
                        }
                        objects = nextObjects
                        delay(16)
                    }
                }

                LaunchedEffect(bombHit) {
                    if (bombHit) {
                        repeat(8) { shakeX = Random.nextInt(-15, 15).toFloat(); delay(16) }
                        shakeX = 0f; bombHit = false
                    }
                }

                // Score submission is intentionally kept in its own effect, separate
                // from the physics/game loop above. Submitting it inline there caused
                // Compose to cancel the in-flight DB write the instant `gameOver`
                // flipped to true (since that's the loop's own key), so the high
                // score never actually persisted.
                LaunchedEffect(gameOver) {
                    if (gameOver) {
                        ServiceLocator.provideCoinRepository().earnFromSession("catch")
                        ServiceLocator.provideCoinRepository().earnFromScore("catch", score)
                        if (scoreRepository.submitScore("catch", score)) requestReview(context)
                    }
                }
                GameScoreOverlay(score, highScore)
            }
            Spacer(Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
            Spacer(Modifier.height(10.dp))
            BannerAdView(
                adUnitId = AdConstants.TEST_BANNER_ID
            )
            Spacer(Modifier.height(22.dp))
        }

        if (gameOver && !showContinueDialog) {
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
                    scope.launch {
                        if (ServiceLocator.provideCoinRepository().spend(ContinueConfig.COST, "continue_catch_the_object")) {
                            continueOffered = true
                            showContinueDialog = false
                            // Remove only the object that killed us (the caught bomb);
                            // everything else — score, basket position, remaining
                            // falling objects — stays exactly as it was.
                            objects = objects.filterNot { it.isCaught && it.type.isBomb }
                            gameOver = false
                        }
                    }
                },
                onWatchAd = {
                    activity?.let {
                        var rewardEarned = false
                        RewardedAdManager.show(
                            activity = it,
                            adUnitId = AdConstants.TEST_REWARDED_ID,
                            source = "continue_catch_the_object_ad",
                            onRewardEarned = { rewardEarned = true },
                            onDismiss = {
                                continueOffered = true
                                showContinueDialog = false
                                if (rewardEarned) {
                                    objects = objects.filterNot { it.isCaught && it.type.isBomb }
                                    gameOver = false
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
        }

        if (gameOver && !showContinueDialog) {
            Column(modifier = Modifier.background(Color.Black.copy(0.7f))) {
                GameOverScreen3(score, highScore) {

                    if (!hasShownInterstitial) {
                        hasShownInterstitial = true

                        activity?.let {
                            InterstitialAdManager.show(it) {
                                objects = emptyList()
                                score = 0
                                gameOver = false
                                continueOffered = false
                                scope.launch { basketScale.snapTo(1f) }
                            }
                        } ?: run {
                            objects = emptyList()
                            score = 0
                            gameOver = false
                            continueOffered = false
                            scope.launch { basketScale.snapTo(1f) }
                        }

                    } else {
                        objects = emptyList()
                        score = 0
                        gameOver = false
                        continueOffered = false
                        scope.launch { basketScale.snapTo(1f) }
                    }
                }

            }
        }
    }
}

/* ------------------ HELPERS ------------------ */



/* ------------------ UI (UNCHANGED) ------------------ */

@Composable
fun GameScoreOverlay(score: Int, highScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Text(
                "SCORE : $score",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF4FB3FF)
            )
        }

        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Text(
                "HIGH : $highScore",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF4FB3FF)
            )
        }
    }
}
@Composable
fun GameOverScreen3(
    score: Int,
    highScore: Int,
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
                Text(
                    text ="“That was a bomb \uD83D\uDE35”",
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
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
            shape = RoundedCornerShape(50)
        ) {
            Text("Play Again", fontSize = 18.sp)

        }

    }
}