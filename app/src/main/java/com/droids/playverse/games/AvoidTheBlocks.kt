package com.droids.playverse.games

import android.R.attr.x
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droids.playverse.AdConstants
import com.droids.playverse.GameOverScreen
import com.droids.playverse.InterstitialAdManager
import com.droids.playverse.R
import com.droids.playverse.data.AvoidBlockHighScorePref
import com.droids.playverse.data.SnakeHighScorePref
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/* ---------------- CONFIG ---------------- */
private const val COLS = 10
private const val BLOCK_HEIGHT = 110f
private const val BLOCK_GAP = 20f
private const val ROCKET_WIDTH = 90f
private const val ROCKET_HEIGHT = 140f

class AvoidTheBlocks : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InterstitialAdManager.load(
            this,
            AdConstants.TEST_INTERSTITIAL_ID
        )
        setContent { AvoidTheBlocksGameUI() }
    }
}
enum class EntityType { BLOCK, STAR }
class GameEntity(val col: Int, var y: Float, val type: EntityType,val color: Color = Color.Transparent,val gradientIndex: Int = 0)

val blockGradients = listOf(
    // 🔴 Red gradient
    listOf(Color(0xFFFF5252), Color(0xFFD32F2F)),

    // 🟠 Orange gradient
    listOf(Color(0xFFFFA726), Color(0xFFF57C00)),

    // 🟡 Yellow gradient
    listOf(Color(0xFFFFEB3B), Color(0xFFFBC02D)),

    // 🟢 Green gradient
    listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),

    // 🔵 Blue gradient
    listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
)


@Preview(showSystemUi = true)
@Composable
fun AvoidTheBlocksGameUI() {
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var gameInstance by remember { mutableIntStateOf(0) }
    val context= LocalContext.current
    var highScore by remember {
        mutableStateOf(AvoidBlockHighScorePref.getHighScore(context))
    }
    var hasShownInterstitial by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    BackHandler(enabled = !isGameOver) {

        if (!hasShownInterstitial) {
            hasShownInterstitial = true

            activity?.let {
                InterstitialAdManager.show(it) {
                    it.finish()
                }
            } ?: run {
                activity?.finish()
            }

        } else {
            activity?.finish()
        }
    }

    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            if (score > highScore) {
                highScore = score
                AvoidBlockHighScorePref.saveHighScore(context, score)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0E4FF))
    ) {
        Spacer(modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(0.01f))
        TopBar("AVOID THE BLOCKS", Color(0xFF2F79C9))
        DividerLine()
        Spacer(Modifier.height(20.dp))
        ScoreRow(score, highScore)

        Box(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            key(gameInstance) {
                GamePlayArea(
                    score=score,
                    isPlaying = !isGameOver,
                    onScoreUpdate = { score += 1 },
                    onGameOver = { isGameOver = true }
                )
            }


        }
        DividerLine()
        Spacer(Modifier.height(10.dp))
        BannerAdView(
            adUnitId = AdConstants.TEST_BANNER_ID
        )
    }
    if (isGameOver) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .blur(16.dp)
        )
    }

            AnimatedVisibility(
                visible = isGameOver,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                GameOverScreen(score, highScore) {

                    if (!hasShownInterstitial) {
                        hasShownInterstitial = true

                        activity?.let {
                            InterstitialAdManager.show(it) {
                                score = 0
                                isGameOver = false
                                gameInstance++
                            }
                        } ?: run {
                            // fallback
                            score = 0
                            isGameOver = false
                            gameInstance++
                        }

                    } else {
                        score = 0
                        isGameOver = false
                        gameInstance++
                    }
                }

            }


    }


@Composable
fun ScoreRow(score: Int, bestScore: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ScoreChip("SCORE: $score")
        ScoreChip("BEST: $bestScore")
    }
}




/* ---------------- MODEL ---------------- */
// Using a standard class (not a data class) to avoid unnecessary overhead
class GameBlock(val col: Int, var y: Float)

@Composable
fun GamePlayArea(score:Int,isPlaying: Boolean, onScoreUpdate: () -> Unit, onGameOver: () -> Unit) {
    val entities = remember { mutableListOf<GameEntity>() }
    val rocketX = remember { Animatable(0f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val scope = rememberCoroutineScope()
    var frameTrigger by remember { mutableLongStateOf(0L) }

    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && rocketX.value == 0f) {
            rocketX.snapTo((canvasSize.width / 2f) - (ROCKET_WIDTH / 2f))
        }
    }
    val dynamicSpeed by rememberUpdatedState(
        when {
            score <= 5  -> 8f
            score <= 10 -> 10f
            score <= 15 -> 14f
            score <= 20 -> 16f
            score<= 25 -> 19f
            score <= 35-> 22f
            score<= 45 -> 24f
            score<=60->26f
            score<= 80 -> 28f
            score<=90->29f
            else        -> 32f   // 🔥 MAX SPEED
        }
    )

    val spawnDelay = 320L

    // Spawning Logic: Mix of Blocks and Stars
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            val type = if (Random.nextFloat() > 0.7f) EntityType.STAR else EntityType.BLOCK
            val gradientIndex = if (type == EntityType.BLOCK) {
                Random.nextInt(blockGradients.size)
            } else 0

            entities.add(
                GameEntity(
                    col = Random.nextInt(0, COLS),
                    y = -BLOCK_HEIGHT,
                    type = type,
                    gradientIndex = gradientIndex
                )
            )
            delay(spawnDelay) // Fast spawning to keep player busy
        }
    }

    // Physics Engine
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            withFrameNanos { time ->
                frameTrigger = time
                if (canvasSize.width <= 0f) return@withFrameNanos

                val cellWidth = (canvasSize.width - (COLS - 1) * BLOCK_GAP) / COLS
                val rocketY = canvasSize.height - 220f
                val iterator = entities.iterator()

                while (iterator.hasNext()) {
                    val entity = iterator.next()
                    entity.y += dynamicSpeed

                    val entityX = entity.col * (cellWidth + BLOCK_GAP)

                    // Collision Check
                    val isColliding = (rocketX.value < entityX + cellWidth && rocketX.value + ROCKET_WIDTH > entityX) &&
                            (rocketY < entity.y + BLOCK_HEIGHT && (rocketY + ROCKET_HEIGHT) > entity.y)

                    if (isColliding) {
                        if (entity.type == EntityType.BLOCK) {
                            SoundManager.playHitSound()
                            onGameOver()
                            return@withFrameNanos
                        } else {
                            SoundManager.playStarSound()
                            onScoreUpdate() // Collected a star!
                            iterator.remove()
                        }
                    } else if (entity.y > canvasSize.height) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1C2D), RoundedCornerShape(24.dp))
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .pointerInput(isPlaying) {
                if (!isPlaying) return@pointerInput
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val targetX =
                        (rocketX.value + dragAmount.x).coerceIn(0f, canvasSize.width - ROCKET_WIDTH)
                    scope.launch { rocketX.snapTo(targetX) }
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.avoidblock_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            val starImage = ImageBitmap.imageResource(R.drawable.star)

            Canvas(modifier = Modifier.fillMaxSize()
                .padding(10.dp)) {

                val drawTrigger = frameTrigger
                if (canvasSize.width <= 0f) return@Canvas

                val cellWidth = (size.width - (COLS - 1) * BLOCK_GAP) / COLS
                val blockScale = 1.4f
                val blockSize = cellWidth * blockScale
                val starSize = 100f

                entities.forEach { entity ->

                    val x = entity.col * (cellWidth + BLOCK_GAP)

                    if (entity.type == EntityType.BLOCK) {

                        // 🔲 Draw block
                        val gradientColors = blockGradients[entity.gradientIndex]

                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = gradientColors,
                                start = Offset(
                                    x,
                                    entity.y
                                ),
                                end = Offset(
                                    x + blockSize,
                                    entity.y + blockSize
                                )
                            ),
                            topLeft = Offset(
                                x - (blockSize - cellWidth) / 2,
                                entity.y - (blockSize - cellWidth) / 2
                            ),
                            size = Size(blockSize, blockSize),
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )

                    } else if (entity.type == EntityType.STAR) {

                        // ⭐ Draw star
                        drawImage(
                            image = starImage,
                            srcOffset = IntOffset.Zero,
                            srcSize = IntSize(starImage.width, starImage.height),
                            dstOffset = IntOffset(
                                (x + cellWidth / 2 - starSize / 2).toInt(),
                                (entity.y + blockSize / 2 - starSize / 2).toInt()
                            ),
                            dstSize = IntSize(
                                starSize.toInt(),
                                starSize.toInt()
                            )
                        )
                    }
                }
            }

            val rocketImage = ImageBitmap.imageResource(id = R.drawable.main_rocket)

                // Rocket
            Canvas(modifier = Modifier.fillMaxSize()) {

                val rocketHeight = size.height * 0.18f
                val aspectRatio = rocketImage.width.toFloat() / rocketImage.height
                val rocketWidth = rocketHeight * aspectRatio

                drawImage(
                    image = rocketImage,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(rocketImage.width, rocketImage.height),
                    dstOffset = IntOffset(
                        rocketX.value.toInt(),
                        (size.height - rocketHeight - 40f).toInt()
                    ),
                    dstSize = IntSize(
                        rocketWidth.toInt(),
                        rocketHeight.toInt()
                    )
                )
            }

        }
    }
}
@Composable
fun ScoreChip(text: String) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(35.dp)
            .background(Color(0xFF2F6BFF), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable
fun DividerLine(){
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(3.dp)
            .background(Color.Black)
    )
}

@Composable
fun GameOverScreen(
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
            color = Color(0xFFFFF200),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
        )
        Image(
            painter = painterResource(R.drawable.crash_avoi),
            contentDescription = "crash_avoid",
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
                    .background(Color.White.copy(alpha = 0.3f))
                    .blur(20.dp)
            )

            // 📝 Sharp text on top
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text ="You crashed into the block !",
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