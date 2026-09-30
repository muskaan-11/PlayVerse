package com.droids.playverse.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droids.playverse.R
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.navigation.GameRoutes
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.AppColors

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val appLink = "https://play.google.com/store/apps/details?id=com.droids.playverse"

    LaunchedEffect(Unit) {
        SoundManager.init(context, isPreview)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            // Home is the root screen, so there's no back button or title here -
            // just the shared coin balance, identical to every other screen.
            TopBar(
                onSettingsClick = { navController.navigate(GameRoutes.SETTINGS) },
                title = "Home",
                onBack = null,
                onCoinBalanceClick = { navController.navigate(GameRoutes.EARN_COINS) }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
                .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                .background(brush = Brush.linearGradient(AppColors.gradientColors2))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MultiColorText()
                Spacer(Modifier.height(20.dp))
                EasySlantedBar()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Player1Game(navController)
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Player2Game(navController)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Hey, I found this app called PlayVerse 🎮 and I’ve been enjoying it lately 😊\n" +
                                        "The games are simple but really engaging 🔥\n" +
                                        "Definitely worth trying! ✨\n\n" +
                                        "👇 CLICK THE LINK BELOW TO DOWNLOAD 👇\n\n${appLink}"
                            )
                        }
                        context.startActivity(
                            Intent.createChooser(shareIntent, "Share via")
                        )
                    },
                    border = BorderStroke(
                        width = 3.dp,
                        color = Color(0xFF97B9E3)
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF119F07),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(0.6f)
                        .padding(start = 10.dp, end = 10.dp, bottom = 15.dp, top = 5.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.share_icon),
                            contentDescription = "img"
                        )
                        Text(
                            text = " SHARE WITH FRIENDS !",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Player1Game(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .fillMaxHeight(0.6f)
            .clip(RoundedCornerShape(topStart = 40.dp, bottomStart = 40.dp))
            .background(Color(0xFFF6F6F6)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.Center)
                .padding(start = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF3F4A7A)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.matchParentSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SlidngEffect(Color(0xFFF29090))
                            Spacer(Modifier.width(10.dp))
                            SlidngEffect(Color(0xFFF29090))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.width(10.dp))
                            SlidngEffect(Color(0xFFF29090))
                            Spacer(Modifier.width(10.dp))
                            SlidngEffect(Color(0xFFF29090))
                        }
                    }
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center),
                        painter = painterResource(R.drawable.num1),
                        contentDescription = "img"
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            SoundManager.playBottleSound()
                            navController.navigate(GameRoutes.ONE_PLAYER_LIST)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF29090),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp, top = 5.dp),
                    ) {
                        Text(
                            text = "PLAYER",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.semicircle),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 5.dp)
        )
    }
}

@Composable
fun Player2Game(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .fillMaxHeight(0.6f)
            .clip(RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp))
            .background(Color(0xFFF6F6F6)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.Center)
                .padding(end = 15.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF3F4A7A)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.matchParentSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.width(10.dp))
                            SlidngEffect(Color(0xFF91B2D9))
                            Spacer(Modifier.width(10.dp))
                            SlidngEffect(Color(0xFF91B2D9))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SlidngEffect(Color(0xFF91B2D9))
                            Spacer(Modifier.width(10.dp))
                            SlidngEffect(Color(0xFF91B2D9))
                        }
                    }
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center),
                        painter = painterResource(R.drawable.num2),
                        contentDescription = "img"
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            SoundManager.playBottleSound()
                            navController.navigate(GameRoutes.TWO_PLAYER_LIST)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF91B2D9),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp, top = 5.dp),
                    ) {
                        Text(
                            text = "PLAYERS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(R.drawable.semi2),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = -5.dp)
            )
            Image(
                painter = painterResource(R.drawable.semi2),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = -5.dp)
            )
        }
    }
}

@Composable
private fun SlidngEffect(color: Color) {
    Row(
        modifier = Modifier
            .width(60.dp)
            .height(5.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color)
    ) {}
}