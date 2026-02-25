package com.droids.playverse.games

import android.R
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.viewinterop.AndroidView
import com.droids.playverse.AdConstants.TEST_BANNER_ID
import com.droids.playverse.AdConstants.TEST_INTERSTITIAL_ID
import com.droids.playverse.InterstitialAdManager
import com.droids.playverse.ui.theme.BgBottom
import com.droids.playverse.ui.theme.BgTop
import com.droids.playverse.ui.theme.BoardColor
import com.droids.playverse.ui.theme.OColor
import com.droids.playverse.ui.theme.XColor
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class TicTacToeActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InterstitialAdManager.load(
            this,
            TEST_INTERSTITIAL_ID
        )

        setContent {
            TicTacToeScreen()
        }
    }
}
// Color Palette



@Preview(showSystemUi = true)
@Composable
fun TicTacToeScreen() {
    var board by remember { mutableStateOf(Array(9) { "" }) }
    var isXTurn by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningIndices by remember { mutableStateOf<List<Int>?>(null) }
    var oScore by remember { mutableIntStateOf(0) }
    var xScore by remember { mutableIntStateOf(0) }
    val BlueShine = Color(0xFF00E5FF) // Vibrant Cyan-Blue
    val RedShine = Color(0xFFFF1744)
    var showContinueDialog by remember { mutableStateOf(false) }
    val context= LocalContext.current
    var gamesPlayed by remember { mutableIntStateOf(0) }
    var hasShownContinueDialog by remember { mutableStateOf(false) }
    var hasShownExitAd by remember { mutableStateOf(false) }

    val activity = context as Activity


    // Animation state for the line (0f to 1f)
    val lineProgress = remember { Animatable(0f) }

    // Animation for the winner banner scaling
    val bannerScale by animateFloatAsState(
        targetValue = if (winner != null) 1.1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bannerScale"
    )

    fun getWinningLine(currentBoard: Array<String>): List<Int>? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (line in lines) {
            if (currentBoard[line[0]].isNotEmpty() &&
                currentBoard[line[0]] == currentBoard[line[1]] &&
                currentBoard[line[0]] == currentBoard[line[2]]
            ) return line
        }
        return null
    }

    fun resetGame() {
        board = Array(9) { "" }
        winner = null
        winningIndices = null
        isXTurn = true
    }
    LaunchedEffect(winner) {
        when (winner) {
            "O", "X" -> {
                SoundManager.playWinSound() // Trigger Win Sound
                lineProgress.animateTo(1f, animationSpec = tween(500))
                kotlinx.coroutines.delay(1500)
            }
            "Draw" -> {
                SoundManager.playDrawSound() // Trigger Draw Sound
                kotlinx.coroutines.delay(2000)
            }
        }
        if (winner != null) {
            gamesPlayed++

            if (gamesPlayed >= 7 && !hasShownContinueDialog) {
                hasShownContinueDialog = true
                showContinueDialog = true
            } else {
                resetGame()
            }
        }

        if (winner == null) {
            lineProgress.snapTo(0f)
        }
    }
    BackHandler {
        if (!hasShownExitAd) {
            hasShownExitAd = true

            InterstitialAdManager.show(activity) {
                activity.finish()
            }
        } else {
            activity.finish()
        }
    }

    if (showContinueDialog) {
        AlertDialog(
            onDismissRequest = {
                showContinueDialog = false
                resetGame()
            },
            title = { Text("Continue Playing?") },
            text = { Text("Hope you’re enjoying the game.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showContinueDialog = false

                        InterstitialAdManager.show(activity) {
                            resetGame()
                        }
                    }
                ) {
                    Text("Continue",
                        color=Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold)
                }
            }
        )
    }


    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE0D7FF), Color(0xFFF3EEFF))))
    ) {
        // --- Top Bar (Remains exactly as provided) ---
        TopBar("TIC TAC TOE", Color(0xFF6A63C5))
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(Modifier.height(2.dp).fillMaxWidth().background(Color.Black))

        // --- Game Area ---
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .background(Brush.verticalGradient(listOf(BgTop, BoardColor, BgBottom)))
        ) {
            Box(modifier = Modifier.size(300.dp).align(Alignment.Center)) {
                // Grid Lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val third = size.width / 3
                    repeat(2) { i ->
                        val pos = third * (i + 1)
                        drawLine(Color.Black, Offset(pos, 0f), Offset(pos, size.height), 8f)
                        drawLine(Color.Black, Offset(0f, pos), Offset(size.width, pos), 8f)
                    }
                }

                // Pieces Logic
                Column {
                    for (row in 0..2) {
                        Row {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                Box(
                                    modifier = Modifier.size(100.dp).clickable {
                                        if (board[index].isEmpty() && winner == null) {
                                            if (isXTurn) SoundManager.play_xSound()
                                            else SoundManager.playBottleSound()
                                            val newBoard = board.copyOf()
                                            newBoard[index] = if (isXTurn) "X" else "O"
                                            board = newBoard

                                            val line = getWinningLine(newBoard)
                                            if (line != null) {
                                                winningIndices = line
                                                winner = newBoard[line[0]]
                                                if (winner == "O") oScore++ else xScore++
                                            } else if (newBoard.none { it.isEmpty() }) {
                                                winner = "Draw"
                                            } else {
                                                isXTurn = !isXTurn
                                            }
                                        }
                                    },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (board[index] == "O") {
                                        OComponent()
                                    }
                                    else if (board[index] == "X") {
                                        XComponent()
                                    }
                                }
                            }
                        }
                    }
                }

                // --- Animated Winning Line ---
                if (winningIndices != null) {
                    val shineColor = if (winner == "O") BlueShine else RedShine
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val third = size.width / 3

                        fun getExtendedPoint(idx: Int, isStart: Boolean): Offset {
                            val centerX = (idx % 3) * third + third / 2
                            val centerY = (idx / 3) * third + third / 2

                            // We calculate the direction vector based on the winning line type
                            // (Horizontal, Vertical, or Diagonal)
                            val startIdx = winningIndices!![0]
                            val endIdx = winningIndices!![2]

                            val dirX = ((endIdx % 3) - (startIdx % 3)).coerceIn(-1, 1)
                            val dirY = ((endIdx / 3) - (startIdx / 3)).coerceIn(-1, 1)

                            // Extension factor (how much longer the line should be)
                            val offsetDist = third * 0.4f

                            return if (isStart) {
                                Offset(centerX - dirX * offsetDist, centerY - dirY * offsetDist)
                            } else {
                                Offset(centerX + dirX * offsetDist, centerY + dirY * offsetDist)
                            }
                        }

                        val startPos = getExtendedPoint(winningIndices!![0], true)
                        val finalEndPos = getExtendedPoint(winningIndices!![2], false)

                        // Progress-based end point for the "drawing" animation
                        val currentEnd = Offset(
                            startPos.x + (finalEndPos.x - startPos.x) * lineProgress.value,
                            startPos.y + (finalEndPos.y - startPos.y) * lineProgress.value
                        )

                        // 1. Outer Glow (Soft & Wide)
                        drawLine(
                            color = shineColor.copy(alpha = 0.3f),
                            start = startPos,
                            end = currentEnd,
                            strokeWidth = 40f,
                            cap = StrokeCap.Round
                        )

                        // 2. Main Color Line
                        drawLine(
                            color = shineColor,
                            start = startPos,
                            end = currentEnd,
                            strokeWidth = 16f,
                            cap = StrokeCap.Round
                        )

                        // 3. Central Shine (White highlight)
                        drawLine(
                            color = Color.White.copy(alpha = 0.8f),
                            start = startPos,
                            end = currentEnd,
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // --- Animated Winner Banner ---
            if (winner != null) {

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp)
                        .scale(bannerScale)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (winner == "Draw") "DRAW!" else "${if (winner == "O") "BLUE" else "RED"} WINS!",
                        color = if (winner == "O") Color(0xFF2F79C9) else Color(0xFFFF5252),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }
            }
        }

        // --- Bottom UI (Score boxes & White Bar) ---
        Column {
            Spacer(Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        .offset(y = -20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreBox(oScore, OColor)
                    ScoreBox(xScore, XColor)
                }

            }
            BannerAdView(adUnitId = TEST_BANNER_ID)
            Spacer(modifier = Modifier.height(15.dp))
        }
    }


}
@Composable
fun OComponent() {
    Canvas(modifier = Modifier.size(60.dp)) {
        drawCircle(color = OColor, style = Stroke(width = 15f))
    }
}

@Composable
fun XComponent() {
    Canvas(modifier = Modifier.size(60.dp)) {
        drawLine(XColor, Offset(0f, 0f), Offset(size.width, size.height), strokeWidth = 15f, cap = StrokeCap.Round)
        drawLine(XColor, Offset(size.width, 0f), Offset(0f, size.height), strokeWidth = 15f, cap = StrokeCap.Round)
    }
}

@Composable
fun ScoreBox(score: Int, color: Color) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(45.dp)
            .background(Color.Black, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(score.toString(), color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable
fun BannerAdView(adUnitId: String) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
