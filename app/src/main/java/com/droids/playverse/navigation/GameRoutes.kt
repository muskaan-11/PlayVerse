package com.droids.playverse.navigation

object GameRoutes {

    const val ARG_GAME_ID = "gameId"

    const val HOME = "home"
    const val ONE_PLAYER_LIST = "one_player_list"
    const val TWO_PLAYER_LIST = "two_player_list"
    const val EARN_COINS = "earn_coins"
    const val SETTINGS = "settings"
    const val PRIVACY_POLICY = "privacy_policy"

    const val INFO = "info/{$ARG_GAME_ID}"
    const val GAME = "game/{$ARG_GAME_ID}"
    const val TWO_PLAYER_GAME = "two_player_game/{$ARG_GAME_ID}"

    fun infoRoute(gameId: String) = "info/$gameId"
    fun gameRoute(gameId: String) = "game/$gameId"
    fun twoPlayerGameRoute(gameId: String) = "two_player_game/$gameId"

    const val TAP_GAME = "game/tap"
    const val GUESS_GAME = "game/guess"
    const val SNAKE_GAME = "game/snake"
    const val AVOID_BLOCKS_GAME = "game/avoid_blocks"
    const val QUIZ_GAME = "game/quiz"
    const val WORD_GAME = "game/word"
    const val BRICK_GAME = "game/brick"
    const val CATCH_GAME = "game/catch"
    const val COMING_SOON = "game/coming_soon"
    const val TIC_TAC_TOE = "game/tic_tac_toe"
    const val MEMORY_MATCH = "game/memory_match"

}