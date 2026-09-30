package com.droids.playverse.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droids.playverse.R
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen(
                onFinished = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun SplashScreen( onFinished: ()->Unit){
    val gradientColors = listOf(
        colorResource(id = R.color.shade1_splash),
        colorResource(id = R.color.shade2_splash)
    )
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(colors=gradientColors)
            )
    ){
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,

        ){
            MultiColorText()
            Spacer(Modifier.height(20.dp))
            EasySlantedBar()
            Spacer(Modifier.height(50.dp))
            IconArea()
            Spacer(Modifier.height( 150.dp))
            SeekBarImplement()
        }



    }

}

@Preview(showSystemUi = true)
@Composable
fun SplashScreenPreview() {
        SplashScreen(onFinished = {})

}
@Composable
fun MultiColorText(){
    Text(
        text= "Play Verse",
        fontFamily = FontFamily(Font(R.font.passero_one_regular)),
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold

    )
}
@Composable
fun EasySlantedBar() {
    val colors = listOf(
        Color(0xFFFF6B6B), Color(0xFF4A90E2), Color(0xFF7ED321),
        Color(0xFFF5A623), Color(0xFFF8E71C)
    )
    val slantedShape = remember {
        GenericShape { size, _ ->
            val slant = 30f
            moveTo(slant, 0f)
            lineTo(size.width + slant, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
    }
    Row(
        modifier = Modifier
            .width(300.dp)
            .height(16.dp)

    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f) // Makes all segments equal width
                    .fillMaxHeight()
                    .background(color, slantedShape)
            )
        }
    }
}
@Composable
fun IconArea() {
    val colorsOfBg = listOf(
        Color(0xFFFF9A9E),
        Color(0xFFFAD0C4),
        Color(0xFFA1C4FD)
    )

    Box(
        Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .align(alignment = Alignment.Center)
                .background(
                    brush = Brush.verticalGradient(colorsOfBg)
                )
        ){}
        Image(
            modifier = Modifier.size(120.dp)
                .align(alignment = Alignment.Center),
            painter=painterResource(R.drawable.game_icon),
            contentDescription = "icon"
        )
    }

}
@Composable
fun SeekBarImplement() {

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(22.dp)
            .clip(RoundedCornerShape(100))
            .background(Color(0xFFB7C4E4)),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = progress.value,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(100)),
            color = Color(0xFF4D65A4),
            trackColor = Color.Transparent
        )

        Text(
            text = "${(progress.value * 100).toInt()}%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
