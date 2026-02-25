package com.droids.playverse

import android.content.Intent
import android.media.SoundPool
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.droids.playverse.data.GameMode
import com.droids.playverse.data.games
import com.droids.playverse.data.two_player_games
import com.droids.playverse.info_screens.InfoScreen
import com.droids.playverse.info_screens.InfoScreenView
import com.droids.playverse.ui.theme.AppColors

class TwoPlayerMain: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TwoplayerMainView()
        }
    }
}
@Preview(showSystemUi = true)
@Composable
fun TwoplayerMainView(){
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
            .padding(top=20.dp)
    ){
        //settings row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f),
            verticalAlignment = Alignment.Bottom
        ) {
            // Left icon
//            IconButton(onClick = {
//                SoundManager.playPop()
//                context.startActivity(Intent(context, MainActivity::class.java))}
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
                    text = "2 PLAYER GAMES",
                    color = Color(0xFF3F4A7A),
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_extrabold))
                )
            }

            // Right spacer (balances the icon)
//            Spacer(modifier = Modifier.size(48.dp))
        }
        Spacer(
            modifier = Modifier.fillMaxWidth()
                .padding(top =10.dp)
                .height(6.dp)
                .background(brush=Brush.verticalGradient(AppColors.gradientColors2))
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .background(brush=Brush.verticalGradient(AppColors.gradientColors2))
        ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(two_player_games.size){
                        index->
                    val game = two_player_games[index]
                    GameCard(game) {
                        val intent  = Intent(context , InfoScreen::class.java)
                        intent.putExtra("game_id",game.id)
                        intent.putExtra("game_mode", GameMode.TWO_PLAYER)
                        context.startActivity(intent)
                    }
                }
            }
        }

    }
}