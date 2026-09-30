package com.droids.playverse.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droids.playverse.R
import com.droids.playverse.model.GameItem
import com.droids.playverse.navigation.GameRoutes
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.AppColors
@Composable
fun InfoScreenView(navController: NavController, game: GameItem){

    Column(
        modifier = Modifier.fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
    ){

        TopBar(
            title = "GAME INFO",
            onBack = { navController.popBackStack() },
            onCoinBalanceClick = { navController.navigate(GameRoutes.EARN_COINS) }
        )
        Spacer(
            modifier = Modifier.fillMaxWidth()
                .padding(top =10.dp)
                .height(4.dp)
                .background(Color(0xFF95D2FE))
        )
        Box(
            modifier = Modifier.fillMaxSize()
                .weight(1f)
                .background(brush=Brush.verticalGradient(AppColors.gradientColors2))
        ) {
            Image(
                painter = painterResource(game.info_image),
                contentDescription = "img",
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "${game.title}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_extrabold)),
                    color = Color(0xFF014CAD),
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                Text(
                    text = "${game.description}",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.poppins_bold)),
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                )
                Spacer(
                    modifier = Modifier.fillMaxHeight(0.4f)
                        .fillMaxWidth()
                )
                Column(
                    modifier = Modifier.size(100.dp)
                        .background(Color.White, shape = CircleShape),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(85.dp),
                        painter = painterResource(R.drawable.smile_icon),
                        contentDescription = "smile"
                    )
                }
                Image(
                    painter = painterResource(game.info_banner),
                    contentDescription = "info_banner",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)   // 80% of screen width
                        .height(80.dp)
                        .offset(y = (-10.dp)),       // increase height
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                        .fillMaxWidth()
                )
                Button(
                    onClick = {
                        startGame(navController, game)
                    },
                    modifier = Modifier.fillMaxWidth(0.3f)
                        .height(50.dp),
                    border = BorderStroke(width = 2.dp, Color(0xFFA6BFF3)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(game.color)
                    )

                ) {
                    Text(
                        text = "PLAY",
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        color = Color.White,
                        fontSize = 20.sp

                    )
                }

            }
        }

        Spacer(
            modifier = Modifier.fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF95D2FE))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        ){}

    }}

fun startGame(navController: NavController, game: GameItem){
    navController.navigate(game.playRoute)
}