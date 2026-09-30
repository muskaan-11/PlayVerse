package com.droids.playverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.droids.playverse.R
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.navigation.GameRoutes
import com.droids.playverse.ui.components.TopBar
import com.droids.playverse.ui.theme.AppColors
import com.droids.playverse.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(AppColors.gradientColors))
    ) {
        TopBar(
            title = "SETTINGS",
            onBack = { navController.popBackStack() },
            onCoinBalanceClick = { navController.navigate(GameRoutes.EARN_COINS) }
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
                .padding(20.dp)
        ) {
            SettingsToggleRow(
                label = "Sound Effects",
                checked = settings.soundEnabled,
                onCheckedChange = settingsViewModel::setSoundEnabled
            )
            Spacer(Modifier.height(16.dp))
            SettingsToggleRow(
                label = "Music",
                checked = settings.musicEnabled,
                onCheckedChange = settingsViewModel::setMusicEnabled
            )
            Spacer(Modifier.height(16.dp))
            SettingsNavigationRow(
                label = "Privacy Policy",
                onClick = {
                    SoundManager.playPop()
                    navController.navigate(GameRoutes.PRIVACY_POLICY)
                }
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF3F4A7A),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.inter_regular)),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3F4A7A),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFB7BFD6)
            )
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF6F6F6))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF3F4A7A),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.inter_regular)),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.back),
            contentDescription = "open",
            tint = Color(0xFF3F4A7A),
            modifier = Modifier
                .size(20.dp)
                .rotate(180f)
        )
    }
}