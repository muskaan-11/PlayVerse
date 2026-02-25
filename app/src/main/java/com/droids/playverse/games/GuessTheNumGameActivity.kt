package com.droids.playverse.games

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.window.Dialog
import com.droids.playverse.AdConstants
import com.droids.playverse.InterstitialAdManager
import com.droids.playverse.R
import kotlinx.coroutines.delay

class GuessTheNumGameActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InterstitialAdManager.load(
            this,
            AdConstants.TEST_INTERSTITIAL_ID
        )
        setContent{
            GuessGame()

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GuessGame(){
    var soundOn by remember { mutableStateOf(true) }
    var guess by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current
    var hasShownInterstitial by remember { mutableStateOf(false) }


    val colors = listOf(
        Color(0xFFE8ECFF),
        Color(0xFFD6E0FF)
    )
    val guessNormal = listOf(
        Color(0xFFC4D2FF),
        Color(0xFF7192FE)
    )
    val guessCorrect= listOf(
        Color(0xFF9EFFCA),
        Color(0xFF13FFA8)
    )
    var showPopup by remember { mutableStateOf(false) }
    var popupMessage by remember { mutableStateOf("") }
    var popupColors by remember {
        mutableStateOf(listOf(Color(0xFFC4D2FF), Color(0xFF7192FE)))
    }
    var showCelebration by remember { mutableStateOf(false) }

    var attempts by remember { mutableIntStateOf(5) }
    var showCorrectDialog by remember { mutableStateOf(false) }
    var showDigits by remember { mutableStateOf(false) }
    var correctNumber by remember {
        mutableIntStateOf((1..100).random())
    }
    Log.d("GuessGame", "\n\nTHE NUMBER IS : $correctNumber\n")

    val digits = if (showDigits) {
        correctNumber
            .toString()
            .padStart(3, ' ')
            .toList()
    } else {
        listOf('0', '0', '0')
    }
    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            delay(2500)
            showCelebration = false
            showCorrectDialog = true       // 3️⃣ open dialog after delay
        }
    }

    BackHandler {
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

    val isCorrectDialog = showCorrectDialog
    val isGameOverDialog = !showCorrectDialog && (attempts == 0|| attempts<0)

    val bgColor = listOf(Color(0xFFE0D7FF),Color(0xFFF3EEFF))

//    DisposableEffect(soundOn) {
//        if (soundOn) {
//            MusicManager.start(context)
//        } else {
//            MusicManager.pause()
//        }
//
//        onDispose {
//            MusicManager.stop()
//        }
//    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(brush = Brush.linearGradient(bgColor))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f),
                verticalAlignment = Alignment.Bottom,
            ) {
//                Spacer(modifier = Modifier.size(48.dp))
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GUESS THE NUMBER",
                        color = Color(0xFF7A6FE8),
                        fontSize = 25.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_extrabold))
                    )
                }
//                IconButton(
//                    onClick = {
////                    soundOn = !soundOn // 🔥 TOGGLE
//                    },
//                    modifier = Modifier
//                        .padding(end = 20.dp)
//                        .size(50.dp)
//                        .background(Color.White, CircleShape)
//                ) {
//                    Icon(
//                        painter = painterResource(
//                            if (soundOn) R.drawable.audio else R.drawable.play_again_icon
//                        ),
//                        contentDescription = "sound toggle",
//                        tint = Color(0xFF7A6FE8),
//                        modifier = Modifier.size(40.dp)
//                    )
//                }
            }
            Spacer(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(3.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .background(brush = Brush.linearGradient(colors))
                    .padding(top = 50.dp)
            ) {
                Column(
                    modifier = Modifier.matchParentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "I’m thinking a number from\n 1-100...\n" +
                                "Guess the number!",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Color(0xFF6B6F99),
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .height(40.dp)
                    )


                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(75.dp),
                        shape = RoundedCornerShape(25.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(
                            3.dp,
                            Color(0xFFAED6FF)
                        )
                    ) {


                        Row(
                            modifier = Modifier.fillMaxSize()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            digits.forEach { digit ->
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(
                                            Color(0xFFBAC4FF),
                                            shape = RoundedCornerShape(5.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (digit == ' ') "" else digit.toString(),
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontSize = 32.sp
                                    )
                                }
                            }


                        }
                    }
                    Spacer(
                        modifier = Modifier.height(50.dp)
                            .fillMaxWidth()
                    )


                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(65.dp),
                        shape = RoundedCornerShape(25.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(
                            3.dp,
                            Color(0xFFAED6FF)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {


                            TextField(
                                value = guess,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() }) {
                                        guess = it
                                        showPopup = false
                                    }
                                },
                                placeholder = {
                                    Text(
                                        text = "Enter Num",
                                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                                        fontSize = 20.sp,
                                        color = Color(0xFFB5C0FF)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .padding(start = 20.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color(0xFFB5C0FF)
                                )
                            )


                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    val userGuess = guess.toIntOrNull() ?: return@Button
                                    val diff = userGuess - correctNumber

                                    when {
                                         diff == 0 ->{
//                                            popupMessage = "Correct. You guessed it right!"
//                                            popupColors = guessCorrect
//                                            attempts = 5
                                            showDigits = true
                                            showPopup = false        // hide normal popup
                                            showCelebration = true // ✅ open dialog

                                        }
                                        else-> {
                                            attempts--
                                            when {
                                                diff > 10 -> {
                                                    popupMessage =
                                                        "Too High! Try a much lower number ⬇️"
                                                    popupColors = guessNormal
                                                    showPopup = true
                                                }

                                                diff in 1..10 -> {
                                                    popupMessage = "Close! Try a little lower 👀"
                                                    popupColors = guessNormal
                                                    showPopup = true
                                                }

                                                diff in -10..-1 -> {
                                                    popupMessage = "Close! Try a little higher 👀"
                                                    popupColors = guessNormal
                                                    showPopup = true
                                                }

                                                diff < -10 -> {
                                                    popupMessage =
                                                        "Too Low! Try a much higher number ⬆️"
                                                    popupColors = guessNormal
                                                    showPopup = true
                                                }
                                            }
                                        }
                                    }


                                },
                                modifier = Modifier.width(130.dp)
                                    .height(42.dp)
                                    .padding(end = 5.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF96A5FF),
                                ),
                                border = BorderStroke(2.dp, Color(0xFF007BF6))

                            ) {
                                Text(
                                    text = "GUESS",
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.poppins_bold))
                                )
                            }

                        }
                    }
                    GuessPopup(
                        message = popupMessage,
                        colors = popupColors,
                        visible = showPopup,
                        duration = 2000,
                        onDismiss = {
                            showPopup = false
                            guess = ""
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    Text(
                        text = "Attempts: ${attempts}",
                        fontSize = 20.sp,
                        color = Color(0xFF6B6F99),
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )
//
//                        Column(
//                            modifier = Modifier.width(260.dp)
//                                .height(30.dp)
//                                .clip(shape = RoundedCornerShape(30.dp))
//                                .background(brush = Brush.verticalGradient(guessNormal)),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "Too High! Try a lower number!",
//                                fontFamily = FontFamily(Font(R.font.inter_regular))
//                            )
//                        }
//                        Spacer(
//                            modifier = Modifier.height(15.dp)
//                        )
//                        Column(
//                            modifier = Modifier.width(260.dp)
//                                .height(30.dp)
//                                .clip(shape = RoundedCornerShape(30.dp))
//                                .background(brush = Brush.verticalGradient(guessNormal)),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "Too Low! Try a higher number!",
//                                fontFamily = FontFamily(Font(R.font.inter_regular))
//                            )
//                        }
//                        Spacer(
//                            modifier = Modifier.height(15.dp)
//                        )
//                        Column(
//                            modifier = Modifier.width(260.dp)
//                                .height(30.dp)
//                                .clip(shape = RoundedCornerShape(30.dp))
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color(0xFF9EFFCA),
//                                            Color(0xFF13FFA8)
//                                        )
//                                    )
//                                ),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "Correct. You guessed it right!",
//                                fontFamily = FontFamily(Font(R.font.inter_regular))
//                            )
//                        }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent // 👈 important
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .width(200.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFF9C8CFF),
                                            Color(0xFF7A6FE8)
                                        )
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.matchParentSize()
                                    .clickable(enabled = true, onClick = {
                                        if (!hasShownInterstitial) {
                                            hasShownInterstitial = true

                                            activity?.let {
                                                InterstitialAdManager.show(it) {
                                                    showCorrectDialog = false
                                                    attempts = 5
                                                    guess = ""
                                                    showDigits = false
                                                    showPopup = false
                                                    correctNumber = (1..100).random()
                                                }
                                            } ?: run {
                                                showCorrectDialog = false
                                                attempts = 5
                                                guess = ""
                                                showDigits = false
                                                showPopup = false
                                                correctNumber = (1..100).random()
                                            }

                                        } else {
                                            showCorrectDialog = false
                                            attempts = 5
                                            guess = ""
                                            showDigits = false
                                            showPopup = false
                                            correctNumber = (1..100).random()
                                        }
                                    }),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center        // 👈 horizontal centering
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.play_again_icon),
                                    contentDescription = "replay",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )

                                Spacer(modifier = Modifier.width(8.dp))            // 👈 space between icon & text

                                Text(
                                    text = "Play Again",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular))
                                )
                            }


                        }

                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .height(20.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier.fillMaxWidth()
                    .height(3.dp)
                    .background(Color.Black)
            )
            Spacer(Modifier.height(12.dp))
            BannerAdView(
                adUnitId = AdConstants.TEST_BANNER_ID
            )
            Spacer(Modifier.height(22.dp))
        }

        if (showCelebration) {
            KonfettiCelebration()
        }
        if (isCorrectDialog || isGameOverDialog) {
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(200.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // 🔹 TITLE
                        Text(
                            text = if (isCorrectDialog) "🎉 Correct Guess!" else "😬 Oops! The number was ${correctNumber}",
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                            color = if (isCorrectDialog)
                                Color(0xFF7A6FE8)
                            else
                                Color(0xFFE53935)
                        )

                        // 🔹 MESSAGE
                        Text(
                            text = if (isCorrectDialog)
                                "You guessed the number correctly 🎯"
                            else
                                "You’ve used all attempts.\nTry again!",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular))
                        )

                        // 🔹 PLAY AGAIN BUTTON (same for both)
                        Button(
                            onClick = {
                                showCorrectDialog = false
                                attempts = 5
                                guess = ""
                                showDigits = false
                                showPopup = false
                                correctNumber = (1..100).random()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7A6FE8)
                            )
                        ) {
                            Text(
                                text = "Play Again",
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.poppins_bold))
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun GuessPopup(
    message: String,
    colors: List<Color>,
    visible: Boolean,
    onDismiss: () -> Unit,
    duration: Long = 2000
) {
    if (visible) {
        LaunchedEffect(visible) {
            delay(duration)
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .width(260.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Brush.verticalGradient(colors)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = message,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }
        }
    }
}
@Composable
fun KonfettiCelebration() {

    KonfettiView(
        modifier = Modifier.fillMaxSize(),
        parties = listOf(

            // 🎊 LEFT POPPER
            Party(
                speed = 12f,
                maxSpeed = 40f,          // 🔥 more energy
                damping = 0.85f,
                angle = 330,
                spread = 80,             // 🌈 wider spread
                colors = listOf(
                    0xfce18a,
                    0xff726d,
                    0xf4306d,
                    0xb48def,
                    0x4dd599
                ),
                shapes = listOf(Shape.Square, Shape.Circle),
                size= listOf(Size.SMALL, Size.MEDIUM),
                position = Position.Relative(0.0, 0.55),
                emitter = Emitter(
                    duration = 2,        // ⏱ longer
                    TimeUnit.SECONDS
                ).perSecond(120)         // 🎊 MORE PIECES
            ),

            // 🎊 RIGHT POPPER
            Party(
                speed = 12f,
                maxSpeed = 40f,
                damping = 0.85f,
                angle = 210,
                spread = 80,
                colors = listOf(
                    0xfce18a,
                    0xff726d,
                    0xf4306d,
                    0xb48def,
                    0x4dd599
                ),
                shapes = listOf(Shape.Square, Shape.Circle),
                size= listOf(Size.SMALL, Size.MEDIUM),
                position = Position.Relative(1.0, 0.55),
                emitter = Emitter(
                    duration = 2,
                    TimeUnit.SECONDS
                ).perSecond(120)
            )
        )
    )
}
