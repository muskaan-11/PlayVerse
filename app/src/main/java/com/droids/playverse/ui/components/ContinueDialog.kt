package com.droids.playverse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.droids.playverse.R
import com.droids.playverse.ui.theme.PlayVerseTheme
import kotlinx.coroutines.delay

private const val AUTO_DECLINE_SECONDS = 10

@Composable
fun ContinueDialog(
    cost: Int,
    coinBalance: Int,
    onContinue: () -> Unit,
    onWatchAd: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsLeft by remember { mutableIntStateOf(AUTO_DECLINE_SECONDS) }
    val canAffordContinue = coinBalance >= cost

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        onDecline()
    }

    Dialog(onDismissRequest = {}) {
        Box(
            modifier = modifier
                .width(300.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Continue?",
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_extrabold)),
                    color = Color(0xFF3F4A7A)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${secondsLeft}s",
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    color = Color(0xFF9AA0B4)
                )

                Spacer(Modifier.height(16.dp))

                BalancePill(balance = coinBalance)

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onContinue,
                    enabled = canAffordContinue,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F4A7A),
                        disabledContainerColor = Color(0xFFC7CBDA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue — $cost", fontFamily = FontFamily(Font(R.font.poppins_bold)))
                    Spacer(Modifier.width(6.dp))
                    CoinGlyph()
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = onWatchAd,
                    enabled = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A9D5D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Watch Ad for Free Continue", fontFamily = FontFamily(Font(R.font.poppins_bold)))
                }
            }
        }
    }
}

@Composable
private fun BalancePill(balance: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF6F5FA), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        CoinGlyph()
        Spacer(Modifier.width(6.dp))
        Text(
            text = balance.toString(),
            color = Color(0xFF5C3D00),
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.poppins_extrabold))
        )
    }
}

@Composable
private fun CoinGlyph() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(Brush.radialGradient(listOf(Color(0xFFFFE066), Color(0xFFFFB300))), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "C",
            color = Color(0xFF8A5A00),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContinueDialogEnoughCoinsPreview() {
    PlayVerseTheme {
        ContinueDialog(
            cost = 50,
            coinBalance = 120,
            onContinue = {},
            onWatchAd = {},
            onDecline = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContinueDialogNotEnoughCoinsPreview() {
    PlayVerseTheme {
        ContinueDialog(
            cost = 50,
            coinBalance = 10,
            onContinue = {},
            onWatchAd = {},
            onDecline = {}
        )
    }
}