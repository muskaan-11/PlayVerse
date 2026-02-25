package com.droids.playverse.info_screens


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import com.droids.playverse.GameCard
import com.droids.playverse.MainActivity
import com.droids.playverse.OnePlayerMain
import com.droids.playverse.R
import com.droids.playverse.data.GameMode
import com.droids.playverse.data.games
import com.droids.playverse.data.two_player_games
import com.droids.playverse.games.AvoidTheBlocks
import com.droids.playverse.games.BrickBreaker
import com.droids.playverse.games.CatchTheObject
import com.droids.playverse.games.GuessGame
import com.droids.playverse.games.GuessTheNumGameActivity
import com.droids.playverse.games.MemoryMatchActivity
import com.droids.playverse.games.SnakeGameActivity
import com.droids.playverse.games.TapGameActivity
import com.droids.playverse.games.TicTacToeActivity
import com.droids.playverse.model.GameItem
import com.droids.playverse.navigation.GameRoutes.AVOID_BLOCKS_GAME
import com.droids.playverse.navigation.GameRoutes.BRICK_GAME
import com.droids.playverse.navigation.GameRoutes.CATCH_GAME
import com.droids.playverse.navigation.GameRoutes.GUESS_GAME
import com.droids.playverse.navigation.GameRoutes.MEMORY_MATCH
import com.droids.playverse.navigation.GameRoutes.SNAKE_GAME
import com.droids.playverse.navigation.GameRoutes.TAP_GAME
import com.droids.playverse.navigation.GameRoutes.TIC_TAC_TOE
import com.droids.playverse.ui.theme.AppColors
class InfoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra("game_id")
        val gameMode = intent.getStringExtra("game_mode")
        val game = when (gameMode) {
            GameMode.ONE_PLAYER -> {
                games.find { it.id == gameId }
            }
            GameMode.TWO_PLAYER -> {
                two_player_games.find { it.id == gameId }
            }
            else -> null
        }

        setContent {
            game?.let {
                InfoScreenView(it)
            }
        }
    }
}
@Composable
fun InfoScreenView(game: GameItem){
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
            .padding(top=20.dp)
    ){

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f),
            verticalAlignment = Alignment.Bottom
        ) {
//            // Left icon
//            IconButton(onClick = {
//                SoundManager.playPop()
//                context.startActivity(Intent(context, OnePlayerMain::class.java))}
//                , modifier = Modifier.padding(start = 20.dp)) {
//                Icon(
//                    painter = painterResource(R.drawable.back),
//                    contentDescription = "cross",
//                    tint = Color(0xFF3F4A7A),
//                    modifier = Modifier.size(40.dp)
//                )
//            }

            // Center text
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GAME INFO",
                    color = Color(0xFF3F4A7A),
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_extrabold))
                )
            }
//
//            // Right spacer (balances the icon)
//            Spacer(modifier = Modifier.size(48.dp))
        }
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
                        startGame(context, game)
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

fun startGame(context: Context, game: GameItem){
        when (game.playRoute) {

            TAP_GAME -> {
                context.startActivity(
                    Intent(context, TapGameActivity::class.java)
                )
            }

            GUESS_GAME -> {
                context.startActivity(
                    Intent(context, GuessTheNumGameActivity::class.java)
                )
            }
            SNAKE_GAME-> {
                context.startActivity(
                    Intent(context, SnakeGameActivity::class.java)
                )
            }
            AVOID_BLOCKS_GAME->{
                context.startActivity(
                    Intent(context, AvoidTheBlocks::class.java)
                )
            }
            BRICK_GAME->{
                context.startActivity(
                    Intent(context, BrickBreaker::class.java)
                )
            }
            CATCH_GAME->{
                context.startActivity(
                    Intent(context, CatchTheObject::class.java)
                )
            }
            TIC_TAC_TOE->{
                context.startActivity(
                    Intent(context, TicTacToeActivity::class.java)
                )
            }
            MEMORY_MATCH->{
                context.startActivity(
                    Intent(context, MemoryMatchActivity::class.java)
                )
            }
        }
}