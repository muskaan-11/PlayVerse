package com.droids.playverse.games

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droids.playverse.AdConstants
import com.droids.playverse.InterstitialAdManager
import com.droids.playverse.OnePlayerMain
import com.droids.playverse.R
import com.droids.playverse.data.HighScorePref
import kotlinx.coroutines.delay
import org.w3c.dom.Text

class TapGameActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InterstitialAdManager.load(
            this,
            AdConstants.TEST_INTERSTITIAL_ID
        )
        setContent {
            TapGameView()
            }
        }
    }

@Preview(showSystemUi = true)
@Composable
fun TapGameView(){
    var soundOn by remember { mutableStateOf(true) }
    var hasShownInterstitial by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    var context = LocalContext.current
    var count by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var timerKey by remember { mutableStateOf(0) }
    DisposableEffect(soundOn) {
        if (soundOn) {
            MusicManager.start(context)
        } else {
            MusicManager.pause()
        }

        onDispose {
            MusicManager.stop()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            HighScorePref.saveHighScore(context, count)
        }
    }
    var highScore by remember {
        mutableStateOf(HighScorePref.getHighScore(context))
    }
    val bgGradient = listOf(
        Color(0xFFFFF9E6),Color(0xFFFFE8B5)
    )
    val count_bg = listOf(
        Color(0xFF6FA8FF),Color(0xFF628BFF)
    )
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

    LaunchedEffect(count) {
        if (count > highScore) {
            highScore = count
            HighScorePref.saveHighScore(context, count)
        }
    }
   Column(
       modifier = Modifier.fillMaxSize()
           .background(Color(0xFFF5F5F5))
   ){
       Row(
           modifier = Modifier
               .fillMaxWidth()
               .fillMaxHeight(0.1f),
           verticalAlignment = Alignment.Bottom,
       ) {
//           Spacer(modifier = Modifier.size(48.dp))
           Box(
               modifier = Modifier
                   .weight(1f),
               contentAlignment = Alignment.Center
           ) {
               Text(
                   text = "TAP COUNTER",
                   color = Color(0xFFF5A623),
                   fontSize = 30.sp,
                   fontFamily = FontFamily(Font(R.font.poppins_extrabold))
               )
           }
//           IconButton(
//               onClick = {
//                   soundOn = !soundOn // 🔥 TOGGLE
//               },
//               modifier = Modifier
//                   .padding(end = 20.dp)
//                   .size(50.dp)
//                   .background(Color.White, CircleShape)
//           ) {
//               Icon(
//                   painter = painterResource(
//                       if (soundOn) R.drawable.audio else R.drawable.play_again_icon
//                   ),
//                   contentDescription = "sound toggle",
//                   tint = Color(0xFFF5A623),
//                   modifier = Modifier.size(40.dp)
//               )
//           }


       }
       Spacer(
           modifier = Modifier.fillMaxWidth()
               .padding(top =10.dp)
               .height(3.dp)
               .background(Color.Black)
       )
       Box(
           modifier = Modifier.fillMaxWidth()
               .fillMaxHeight(0.9f)
               .background(brush = Brush.linearGradient(bgGradient))
               .padding(top = 50.dp)
       ){
           Column(
               modifier = Modifier.matchParentSize()
                   , horizontalAlignment = Alignment.CenterHorizontally
           ) {


               Row(
                   modifier = Modifier.fillMaxWidth()
                       .fillMaxHeight(0.15f)
                       .padding(start = 10.dp, end = 10.dp),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   Row(
                       modifier = Modifier
                           .width(125.dp)
                           .height(60.dp)
                           .background(
                               color = Color(0xFFF5F5F5),
                               shape = RoundedCornerShape(20.dp)
                           )
                           .border(
                               BorderStroke(1.dp, Color(0xFFB8B8B8)),
                               shape = RoundedCornerShape(20.dp)
                           ), verticalAlignment = Alignment.CenterVertically,
                       horizontalArrangement = Arrangement.Center
                   ) {
                       Image(
                           painterResource(R.drawable.time),
                           contentDescription = "time",
                           modifier = Modifier.size(45.dp)
                       )

                       TimeCounter(
                           key = timerKey,
                           onTimeUp = { gameOver = true
                               HighScorePref.saveHighScore(context, count)}
                       )
                   }

                   Row(
                       modifier = Modifier
                           .width(125.dp)
                           .height(60.dp)
                           .background(
                               color = Color(0xFFF5F5F5),
                               shape = RoundedCornerShape(20.dp)
                           )
                           .border(
                               BorderStroke(1.dp, Color(0xFFB8B8B8)),
                               shape = RoundedCornerShape(20.dp)
                           ), verticalAlignment = Alignment.CenterVertically,
                       horizontalArrangement = Arrangement.Center
                   ) {

                       Text(
                           text = "HIGH :  ",
                           color = Color.Black,
                           fontWeight = FontWeight.Bold,
                           fontSize = 20.sp,
                           fontFamily = FontFamily(Font(R.font.poppins_bold))
                       )
                       Text(
                           text = highScore.toString(),
                           color = Color(0xFFF5A623),
                           fontWeight = FontWeight.Bold,
                           fontSize = 20.sp,
                           fontFamily = FontFamily(Font(R.font.poppins_bold))
                       )
                   }
               }

               //tap area

               Spacer(modifier = Modifier.height(40.dp)
                   .fillMaxWidth())
               val shape = RoundedCornerShape(20.dp)

               Column(
                   modifier = Modifier
                       .height(160.dp)
                       .width(245.dp)
                       .shadow(
                           elevation = 22.dp,
                           shape = shape,
                           ambientColor =Color(0xFF6FA8FF) ,
                       )
                       .clip(shape)
                       .background(
                           brush = Brush.verticalGradient(count_bg),
                           shape = shape
                       )

                   , horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.Center
               ) {
                   Text(text = "${count}" ,
                       fontSize = 100.sp,
                       color =  Color.White,
                       fontFamily = FontFamily(Font(R.font.inter_regular)),
                   )
               }
               Spacer(modifier = Modifier.height(60.dp)
                   .fillMaxWidth())
               Button(
                   onClick = { count++},
                   colors = ButtonDefaults.buttonColors(
                       containerColor = Color.Transparent // 👈 important
                   ),
                   contentPadding = PaddingValues(0.dp),
                   modifier = Modifier
                       .height(70.dp)
                       .width(230.dp)
               ) {
                   Box(
                       modifier = Modifier
                           .fillMaxSize()
                           .background(
                               brush = Brush.horizontalGradient(
                                   colors = listOf(
                                       Color(0xFFFFC83D),
                                       Color(0xFFFF9F1C)
                                   )
                               ),
                               shape = RoundedCornerShape(25.dp)
                           ),
                       contentAlignment = Alignment.Center
                   ) {
                       Text(
                           text = "TAP !",
                           color = Color.White,
                           fontWeight = FontWeight.Bold,
                           fontSize = 34.sp,
                           fontFamily = FontFamily(Font(R.font.poppins_bold))
                       )
                   }
               }
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
                               brush = Brush.horizontalGradient(
                                   colors = listOf(
                                       Color(0xFFFFB347),
                                       Color(0xFFFF8C00)
                                   )
                               ),
                               shape = RoundedCornerShape(25.dp)
                           ),
                       contentAlignment = Alignment.Center
                   ) {
                       Row(
                           modifier = Modifier.matchParentSize()
                               .clickable(enabled = true, onClick = {
                                   gameOver = false
                                   count = 0
                                   timerKey++
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
       Spacer(Modifier.height(10.dp))
       BannerAdView(
           adUnitId = AdConstants.TEST_BANNER_ID
       )
   }
    if (gameOver) {
        GameOverDialog(
            score = count,
            onPlayAgain = {

                if (!hasShownInterstitial) {
                    hasShownInterstitial = true

                    activity?.let {
                        InterstitialAdManager.show(it) {
                            gameOver = false
                            count = 0
                            timerKey++
                        }
                    } ?: run {
                        gameOver = false
                        count = 0
                        timerKey++
                    }

                } else {
                    gameOver = false
                    count = 0
                    timerKey++
                }
            }
        )
    }


}

@Composable
fun TimeCounter(key : Int,onTimeUp:()->Unit){
   var seconds by remember(key) { mutableStateOf(15) }

    LaunchedEffect(key) {
        while(seconds>0){
            delay(1000)
            seconds--
        }
        onTimeUp()
    }
    Text(
        text = "$seconds",
        color = Color(0xFFF5A623),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        fontFamily = FontFamily(Font(R.font.poppins_bold))
    )
}



@Composable
fun GameOverDialog(
    score: Int,
    onPlayAgain: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .width(260.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Game Over",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF5A623)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Score: $score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPlayAgain,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Play Again")
                }
            }
        }
    }
}
