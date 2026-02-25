package com.droids.playverse

import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droids.playverse.AdConstants.TEST_BANNER_ID
import com.droids.playverse.data.HighScorePref
import com.droids.playverse.data.SnakeHighScorePref
import com.droids.playverse.games.BannerAdView
import com.droids.playverse.ui.theme.Citrine
import com.droids.playverse.ui.theme.Green

@Composable
fun SnakeGameScreen(
    state: SnakeGameState,
    onEvent: (SnakeGameEvent) -> Unit
) {
    var hasShownInterstitial by remember { mutableStateOf(false) }
    val activity = LocalActivity.current

    /* ---------- RESOURCES ---------- */
    val foodBitmap = ImageBitmap.imageResource(R.drawable.img_apple)
    val headBitmap = when (state.direction) {
        Direction.RIGHT -> ImageBitmap.imageResource(R.drawable.right_snake)
        Direction.LEFT -> ImageBitmap.imageResource(R.drawable.left_snkae)
        Direction.UP -> ImageBitmap.imageResource(R.drawable.up_snake)
        Direction.DOWN -> ImageBitmap.imageResource(R.drawable.down_snake)
    }

    var  score= state.snake.size-1
    var context = LocalContext.current
    var highScore by remember {
        mutableStateOf(SnakeHighScorePref.getHighScore(context))
    }
    var isNewHighScore by remember {
        mutableStateOf(false)
    }
    BackHandler(enabled = !state.isGameOver) {

        if (!hasShownInterstitial) {
            hasShownInterstitial = true

            activity?.let {
                InterstitialAdManager.show(it) {
                    it.finish()
                }
            } ?: run {
                // Activity is null → exit safely
                // (Do NOT try to show ad)
            }

        } else {
            activity?.finish()
        }
    }


    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            if (score > highScore) {
                isNewHighScore = true
                highScore = score
                SnakeHighScorePref.saveHighScore(context, score)
            } else {
                isNewHighScore = false
            }
        }
    }

    val foodSound = remember { MediaPlayer.create(context, R.raw.food) }
    val gameOverSound = remember { MediaPlayer.create(context, R.raw.gameover) }

    LaunchedEffect(state.snake.size) {
        if (state.snake.size > 1) foodSound.start()
    }

    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) gameOverSound.start()
    }

    /* ---------- UI ---------- */
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFD2FFE7),
                            Color(0xFF9FD6B3)
                        )
                    )
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Spacer(modifier=Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "SNAKE BITE",
                    color = Color(0xFF004C25),
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_extrabold))
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Tap on the board to move the\nsnake in that direction",
                    color = Color(0xFF4CAF50),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    textAlign = TextAlign.Center
                )
            }


//                IconButton(
//                    onClick = { /* sound toggle later */ },
//                    modifier = Modifier
//                        .padding(end = 20.dp)
//                        .size(50.dp)
//                        .background(Color.White, CircleShape)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.audio),
//                        contentDescription = "sound",
//                        tint = Color(0xFF004C25),
//                        modifier = Modifier.size(40.dp)
//                    )
//                }


            /* ---------- GAME BOARD ---------- */
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
                    .pointerInput(state.gameState) {
                        if (state.gameState == GameState.STARTED) {
                            detectTapGestures { offset ->
                                onEvent(
                                    SnakeGameEvent.UpdateDirection(
                                        offset,
                                        size.width
                                    )
                                )
                            }
                        }
                    }
            ) {
                val cellSize = size.width / 15

                drawBoard(cellSize, Green, state.xAxisGridSize, state.yAxisGridSize)

                drawFood(foodBitmap, cellSize.toInt(), state.food)

                drawSnake(headBitmap, cellSize, state.snake)

            }



            /* ---------- SCORE ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.CenterHorizontally)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color(0xFFF5F0F0), shape = RoundedCornerShape(20.dp))
                    .border(2.dp, color = Color(0xFF397052), shape = RoundedCornerShape(20.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Score: ${state.snake.size - 1}",
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontSize = 16.sp
                )
            }

            /* ---------- BUTTONS ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                GameButton(
                    text = when (state.gameState) {
                        GameState.IDLE -> "Start"
                        GameState.STARTED -> "Pause"
                        GameState.PAUSED -> "Resume"
                    },
                    enabled = !state.isGameOver,
                    width = 300.dp,
                    onClick = {
                        when (state.gameState) {
                            GameState.IDLE, GameState.PAUSED ->
                                onEvent(SnakeGameEvent.StartGame)

                            GameState.STARTED ->
                                onEvent(SnakeGameEvent.PauseGame)
                        }
                    }
                )
            }
            Spacer(
                modifier = Modifier.height(30.dp)
            )

        }

    }
    if (state.isGameOver) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .blur(16.dp)
        )
    }


    // 💥 Game Over Screen
    AnimatedVisibility(
        visible = state.isGameOver,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        GameOverScreen(
            score = state.snake.size - 1,
            highScore = highScore,
            isNewHighScore = isNewHighScore,
            onPlayAgain = {
                if (!hasShownInterstitial && activity != null) {
                    hasShownInterstitial = true

                    InterstitialAdManager.show(activity) {
                        onEvent(SnakeGameEvent.ResetGame)
                    }
                } else {
                    onEvent(SnakeGameEvent.ResetGame)
                }
            }
        )
    }
}
@Composable
fun GameOverScreen(
    score: Int,
    highScore: Int,
    isNewHighScore: Boolean,
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
            painter = painterResource(R.drawable.snake_end),
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
                    .background(Color.White.copy(alpha = 0.3f))
                    .blur(20.dp)
            )
            var gameOverMessage = when {
                isNewHighScore -> "🏆 NEW HIGH SCORE!"
                score == 0 -> "Warm up round! Try again !"
                score < 5 -> "Nice start! Keep going"
                else -> "Good run! Can you beat your best?"
            }

            // 📝 Sharp text on top
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text =gameOverMessage,
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
/* ---------- BUTTON ---------- */
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
                        Color(0xFF85D2A2),
                        Color(0xFF52966F)
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

/* ---------- DRAW HELPERS ---------- */
private fun DrawScope.drawBoard(
    cellSize: Float,
    cellColor: Color,
    gridWidth: Int,
    gridHeight: Int
) {
    for (i in 0 until gridWidth) {
        for (j in 0 until gridHeight) {
            val isBorderCell = i == 0 || j == 0 || i == gridWidth - 1 || j == gridHeight - 1
            drawRect(
                color = if(isBorderCell) Color.Black
                else if ((i + j) % 2 == 0) cellColor
                else cellColor.copy(alpha = 0.8f),
                topLeft = Offset(i * cellSize, j * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun DrawScope.drawFood(
    image: ImageBitmap,
    cellSize: Int,
    coordinate: Coordinate
) {
    drawImage(
        image,
        dstOffset = IntOffset(coordinate.x * cellSize, coordinate.y * cellSize),
        dstSize = IntSize(cellSize, cellSize)
    )
}

private fun DrawScope.drawSnake(
    head: ImageBitmap,
    cellSize: Float,
    snake: List<Coordinate>
) {
    val intSize = cellSize.toInt()
    snake.forEachIndexed { index, c ->
        if (index == 0) {
            drawImage(
                head,
                dstOffset = IntOffset(c.x * intSize, c.y * intSize),
                dstSize = IntSize(intSize, intSize)
            )
        } else {
            drawCircle(
                color = Color(0xFF72C311),
                radius = cellSize / 2,
                center = Offset(
                    c.x * cellSize + cellSize / 2,
                    c.y * cellSize + cellSize / 2
                )
            )
        }
    }
}
