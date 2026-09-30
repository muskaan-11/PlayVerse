package com.droids.playverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.data.two_player_games
import com.droids.playverse.navigation.GameRoutes
import com.droids.playverse.ui.components.GameCard
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.AppColors

@Preview(showSystemUi = true)
@Composable
fun TwoplayerMainView(navController: NavController = rememberNavController()){

    Column(
        modifier = Modifier.fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
    ){
        TopBar(
            title = "2 PLAYER GAMES",
            onBack = { navController.popBackStack() },
            onCoinBalanceClick = { navController.navigate(GameRoutes.EARN_COINS) }
        )
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
                        SoundManager.playPop()
                        navController.navigate(GameRoutes.infoRoute(game.id))
                    }
                }
            }
        }

    }
}