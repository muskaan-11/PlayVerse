package com.droids.playverse.data

import android.content.Context
import androidx.compose.runtime.snapshots.toInt
import androidx.compose.ui.graphics.Color
import com.droids.playverse.R
import com.droids.playverse.model.GameItem
import com.droids.playverse.navigation.GameRoutes

val games = listOf(




    GameItem(
        id = "snake",
        title = "Snake Bite",
        image = R.drawable.snake_bite_image,
        description = "Control the snake and collect food to grow longer.\n Avoid hitting walls or yourself as the game speeds up.",
        color = 0xFF547024.toInt(),
        playRoute = GameRoutes.SNAKE_GAME,
        info_image = R.drawable.snake_info_bg,
        info_banner = R.drawable.snake_info_banner
    ),

    GameItem(
        id = "avoid",
        title = "Avoid The Blocks",
        image = R.drawable.avoid_the_blocks,
        description = "Move fast and dodge falling obstacles.\n Survive as long as possible while the game gets harder over time.",
        color = 0xFFEA2F03.toInt(),
        playRoute = GameRoutes.AVOID_BLOCKS_GAME,
        info_image = R.drawable.avoid_info_bg,
        info_banner = R.drawable.avoid_info_banner
    ),
    GameItem(
        id = "tap",
        title = "Tap Counter",
        image = R.drawable.tap_counter_image,
        description = "Tap as fast as you can before time runs out!\n Test your speed and reflexes by tapping repeatedly to achieve the highest score possible.",
        color = 0xFF0B5CC6.toInt(),
        playRoute = GameRoutes.TAP_GAME,
        info_image = R.drawable.tap_bg_info,
        info_banner = R.drawable.tap_info_banner
    ),
    GameItem(
        id = "brick",
        title = "Brick Breaker",
        image = R.drawable.break_the_brick_image,
        description = "Break the bricks with the ball and paddle!\nMove the paddle left and right to bounce the ball and break the bricks.",
        color = 0xFFA8816F.toInt(),
        playRoute = GameRoutes.BRICK_GAME,
        info_image = R.drawable.brick_info_bg,
        info_banner = R.drawable.brick_info_banner
    ),

    GameItem(
        id = "catch",
        title = "Catch The Object",
        image = R.drawable.catch_the_object_image,
        description = "Catch the falling objects to score points!\nMove left or right to catch the correct objects and earn the highest score.",
        color = 0xFFFFC926.toInt(),
        playRoute = GameRoutes.CATCH_GAME,
        info_image = R.drawable.catch_info_bg,
        info_banner = R.drawable.catch_info_banner
    ),
    GameItem(
        id = "guess",
        title = "Guess The Number",
        image = R.drawable.guess_the_number,
        description = "Can you find the hidden number? \n Use logic and hints to guess the correct number in the fewest tries.",
        color = 0xFF8738ED.toInt(),
        playRoute = GameRoutes.GUESS_GAME,
        info_image = R.drawable.guess_info_bg,
        info_banner = R.drawable.guess_info_banner
    ),

    GameItem(
        id = "coming",
        title = "Coming Soon",
        image = R.drawable.coming_soon,
        description = "More exciting games are on the way!",
        color = 0xFF38CEFF.toInt(),
        playRoute = GameRoutes.COMING_SOON,
        info_image = R.drawable.coming_soon,
        info_banner = R.drawable.coming_soon
    )

)
val two_player_games = listOf(
    GameItem(
        id = "tic_tac_toe",
        title = "Tic Tac Toe",
        image = R.drawable.tic_tac_toe,
        description = "Get three in a row to win!\n" +
                "Play against each other in the classic game of Tic tac toe and get three in a row to secure your victory.",
        color = 0xFF0B5CC6.toInt(),
        playRoute = GameRoutes.TIC_TAC_TOE,
        info_image = R.drawable.tic_tac_toe_info_bg,
        info_banner = R.drawable.tic_tac_toe_info_banner
    ),
    GameItem(
        id = "memory_match",
        title = "Memory Match",
        image = R.drawable.memory_match_image,
        description = "Flip the cards and find matching pairs.\nImprove your memory and concentration by remembering card positions and matching them correctly.",
        color = 0xFFFFC926.toInt(),
        playRoute = GameRoutes.MEMORY_MATCH,
        info_image = R.drawable.memory_info_bg,
        info_banner = R.drawable.memory_info_banner
    ),
    GameItem(
        id = "coming",
        title = "Coming Soon",
        image = R.drawable.coming_soon,
        description = "More exciting games are on the way!",
        color = 0xFF38CEFF.toInt(),
        playRoute = GameRoutes.COMING_SOON ,
        info_image = R.drawable.coming_soon,
        info_banner = R.drawable.coming_soon

    )
)
object GameMode{
    const val ONE_PLAYER = "one_player"
    const val TWO_PLAYER = "two_player"

}

object HighScorePref{
    private const val PREF_NAME = "pref_name"
    private const val KEY_HIGH_SCORE ="high_score"

    fun saveHighScore(context: Context, score:Int ){
        val pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        val currentHigh = pref.getInt(KEY_HIGH_SCORE,0)

        if(score>currentHigh){
            pref.edit().putInt(KEY_HIGH_SCORE,score).apply()
        }
    }
    fun getHighScore(context: Context): Int {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(KEY_HIGH_SCORE, 0)
    }
}
object SnakeHighScorePref{
    private const val SNAKE_PREF_NAME = "snake_high_score_pref"
    private const val SNAKE_KEY_HIGH_SCORE = "snake_high_score"

    fun saveHighScore(context: Context, score:Int ){
        val pref = context.getSharedPreferences(SNAKE_PREF_NAME,Context.MODE_PRIVATE)
        val currentHigh = pref.getInt(SNAKE_KEY_HIGH_SCORE,0)

        if(score>currentHigh){
            pref.edit().putInt(SNAKE_KEY_HIGH_SCORE,score).apply()
        }
    }
    fun getHighScore(context: Context): Int {
        val pref = context.getSharedPreferences(SNAKE_PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(SNAKE_KEY_HIGH_SCORE, 0)
    }
}
object AvoidBlockHighScorePref{
    private const val AVOID_PREF_NAME = "avoid_high_score_pref"
    private const val AVOID_KEY_HIGH_SCORE = "avoid_high_score"

    fun saveHighScore(context: Context, score:Int ){
        val pref = context.getSharedPreferences(AVOID_PREF_NAME,Context.MODE_PRIVATE)
        val currentHigh = pref.getInt(AVOID_KEY_HIGH_SCORE,0)

        if(score>currentHigh){
            pref.edit().putInt(AVOID_KEY_HIGH_SCORE,score).apply()
        }
    }
    fun getHighScore(context: Context): Int {
        val pref = context.getSharedPreferences(AVOID_PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(AVOID_KEY_HIGH_SCORE, 0)
    }
}
object BrickBreakHighScorePref{
    private const val BRICK_PREF_NAME = "brick_high_score_pref"
    private const val BRICK_KEY_HIGH_SCORE = "brick_high_score"

    fun saveHighScore(context: Context, score:Int ){
        val pref = context.getSharedPreferences(BRICK_PREF_NAME,Context.MODE_PRIVATE)
        val currentHigh = pref.getInt(BRICK_KEY_HIGH_SCORE,0)

        if(score>currentHigh){
            pref.edit().putInt(BRICK_KEY_HIGH_SCORE,score).apply()
        }
    }
    fun getHighScore(context: Context): Int {
        val pref = context.getSharedPreferences(BRICK_PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(BRICK_KEY_HIGH_SCORE, 0)
    }
}
object CatchTheObjectHighScorePref{
    private const val CATCH_OBJECT_PREF_NAME = "catch_obj_score_pref"
    private const val CATCH_OBJECT_HIGH_SCORE = "catch_obj_score"

    fun saveHighScore(context: Context, score:Int ){
        val pref = context.getSharedPreferences(CATCH_OBJECT_PREF_NAME,Context.MODE_PRIVATE)
        val currentHigh = pref.getInt(CATCH_OBJECT_HIGH_SCORE,0)

        if(score>currentHigh){
            pref.edit().putInt(CATCH_OBJECT_HIGH_SCORE,score).apply()
        }
    }
    fun getHighScore(context: Context): Int {
        val pref = context.getSharedPreferences(CATCH_OBJECT_PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(CATCH_OBJECT_HIGH_SCORE, 0)
    }
}