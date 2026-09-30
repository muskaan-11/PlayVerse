package com.droids.playverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droids.playverse.R
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.AppColors

/**
 * Static privacy policy screen. The copy below is a generic placeholder — replace it
 * (or swap this Column for a WebView pointing at your hosted policy URL) with your
 * actual, reviewed policy text before shipping. This is a content dependency, not a
 * code dependency: the app team needs to supply/host the real policy.
 */
private const val LAST_UPDATED = "August 31, 2026"

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
    ) {
        TopBar(
            title = "PRIVACY POLICY",
            onBack = { navController.popBackStack() }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(4.dp)
                .background(Color(0xFF95D2FE))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(AppColors.gradientColors2))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            PolicyBody()
        }
    }
}

@Composable
private fun PolicyBody() {
    Text(
        text = "Last updated: $LAST_UPDATED",
        color = Color(0xFF6B6F99),
        fontSize = 13.sp,
        fontFamily = FontFamily(Font(R.font.inter_regular))
    )
    Spacer(Modifier.height(16.dp))

    PolicySection(
        heading = "Overview",
        body = "PlayVerse (\"we\", \"our\", \"the app\") respects your privacy. This policy " +
                "explains what information the app collects, why, and the choices you have. " +
                "Replace this placeholder text with your reviewed, legally accurate policy " +
                "before publishing the app."
    )

    PolicySection(
        heading = "Information We Collect",
        body = "PlayVerse stores your game scores, coin balance, and settings locally on " +
                "your device. We do not require an account or collect personal information " +
                "such as your name, email address, or phone number to play."
    )

    PolicySection(
        heading = "Advertising & Analytics",
        body = "This app shows ads served by Google's advertising services and uses " +
                "Google Analytics/Firebase for basic app analytics. These services may " +
                "collect device identifiers and usage data as described in Google's own " +
                "privacy policy. Where required (for example, in the EU/EEA and UK), we ask " +
                "for your consent before showing personalized ads via Google's User " +
                "Messaging Platform consent form."
    )

    PolicySection(
        heading = "Children's Privacy",
        body = "PlayVerse is not directed at children under 13, and we do not knowingly " +
                "collect personal information from children. If you believe a child has " +
                "provided us with personal information, please contact us so we can remove it."
    )

    PolicySection(
        heading = "Data Retention & Deletion",
        body = "Gameplay data (scores, coins, settings) is stored on your device and is " +
                "removed automatically when you uninstall the app. You can also clear it at " +
                "any time from Android's App Info \u2192 Storage \u2192 Clear data screen."
    )

    PolicySection(
        heading = "Your Choices",
        body = "You can review or withdraw your ad-consent choices at any time from the " +
                "Settings screen, and you can control ad personalization from your device's " +
                "Google Ads Settings."
    )
}

@Composable
private fun PolicySection(heading: String, body: String) {
    Text(
        text = heading,
        color = Color(0xFF3F4A7A),
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.inter_regular))
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = body,
        color = Color(0xFF4A4A4A),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFamily = FontFamily(Font(R.font.inter_regular))
    )
    Spacer(Modifier.height(18.dp))
}