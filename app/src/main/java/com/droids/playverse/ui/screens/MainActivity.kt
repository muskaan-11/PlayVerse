package com.droids.playverse.ui.screens

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.ads.ConsentManager
import com.droids.playverse.navigation.NavGraph
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewManagerFactory

class MainActivity : ComponentActivity() {

    // Read/written both from the (non-Compose) back-press callback and from the
    // Compose tree below, which is fine — SnapshotMutableState works as a plain
    // field and still triggers recomposition wherever it's read inside @Composable.
    private var showExitDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Resolve UMP consent before requesting any ad. On an EU/EEA test device this
        // shows a consent form; everywhere else it resolves right away. Only once this
        // completes do we initialize the Ads SDK, so no ad request fires beforehand.
        ConsentManager.requestConsent(this) {
            if (ConsentManager.canRequestAds(this)) {
                MobileAds.initialize(this)
            }
        }

        // Back on Home (nothing left for the NavHost to pop) now asks for confirmation
        // instead of exiting immediately. Deeper screens are handled first by the
        // NavHost's own back stack and by each game's own BackHandler.
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitDialog = true
                }
            })

        setContent {
            NavGraph()

            if (showExitDialog) {
                ExitConfirmationDialog(
                    onConfirmExit = { finish() },
                    onDismiss = { showExitDialog = false }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release()
    }
}

@Composable
private fun ExitConfirmationDialog(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Exit PlayVerse?") },
        text = { Text("Are you sure you want to close the app?") },
        confirmButton = {
            TextButton(onClick = onConfirmExit) {
                Text("Exit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Requests a Play Store in-app review. Call this from a genuine high-score / "win"
 * moment (see the game screens), never from the exit path — Google's guidance is to
 * ask at a positive moment, not right before the user leaves.
 */
fun requestReview(context: Context) {
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val alreadyRated = sharedPref.getBoolean("rated", false)

    if (alreadyRated) return

    val reviewManager = ReviewManagerFactory.create(context)
    val request = reviewManager.requestReviewFlow()

    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(context as Activity, reviewInfo)

            flow.addOnCompleteListener {
                sharedPref.edit().putBoolean("rated", true).apply()
            }
        }
    }
}