package com.droids.playverse.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.droids.playverse.ui.screens.OneplayerMainView
import com.droids.playverse.ui.screens.TwoplayerMainView
import com.droids.playverse.data.games
import com.droids.playverse.data.two_player_games
import com.droids.playverse.games.AvoidTheBlocksGameUI
import com.droids.playverse.games.BrickBreakerScreen
import com.droids.playverse.games.CatchTheObjectGame
import com.droids.playverse.games.GuessGame
import com.droids.playverse.games.MemoryMatchScreen
import com.droids.playverse.games.snakeGame.SnakeGame
import com.droids.playverse.games.TapGameView
import com.droids.playverse.games.TicTacToeScreen
import com.droids.playverse.ui.screens.InfoScreenView
import com.droids.playverse.ui.screens.EarnCoinsScreen
import com.droids.playverse.ui.screens.HomeScreen
import com.droids.playverse.ui.screens.PrivacyPolicyScreen
import com.droids.playverse.ui.screens.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = GameRoutes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(GameRoutes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(GameRoutes.ONE_PLAYER_LIST) {
            OneplayerMainView(navController = navController)
        }

        composable(GameRoutes.TWO_PLAYER_LIST) {
            TwoplayerMainView(navController = navController)
        }

        composable(GameRoutes.INFO) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString(GameRoutes.ARG_GAME_ID).orEmpty()
            val game = (games + two_player_games).find { it.id == gameId }

            if (game != null) {
                InfoScreenView(navController = navController, game = game)
            } else {
                PlaceholderScreen(title = "Info", gameId = gameId)
            }
        }

        composable(GameRoutes.GAME) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString(GameRoutes.ARG_GAME_ID).orEmpty()
            val onExit: () -> Unit = { navController.popBackStack() }

            when (gameId) {
                "tap" -> TapGameView(onExit = onExit)
                "guess" -> GuessGame(onExit = onExit)
                "snake" -> SnakeGame(onExit = onExit)
                "avoid_blocks" -> AvoidTheBlocksGameUI(onExit = onExit)
                "brick" -> BrickBreakerScreen(onExit = onExit)
                "catch" -> CatchTheObjectGame(onExit = onExit)
                "tic_tac_toe" -> TicTacToeScreen(onExit = onExit)
                "memory_match" -> MemoryMatchScreen(onExit = onExit)
                else -> PlaceholderScreen(title = "Game", gameId = gameId)
            }
        }

        composable(GameRoutes.TWO_PLAYER_GAME) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString(GameRoutes.ARG_GAME_ID).orEmpty()
            PlaceholderScreen(title = "Two Player Game", gameId = gameId)
        }

        composable(GameRoutes.EARN_COINS) {
            EarnCoinsScreen(navController = navController)
        }

        composable(GameRoutes.SETTINGS) {
            SettingsScreen(navController = navController)
        }

        composable(GameRoutes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(navController = navController)
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, gameId: String? = null) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (gameId != null) "$title ($gameId)" else title)
        }
    }
}