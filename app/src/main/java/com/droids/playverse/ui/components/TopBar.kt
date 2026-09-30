package com.droids.playverse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droids.playverse.R
import com.droids.playverse.sound.SoundManager
import com.droids.playverse.ui.viewmodel.CoinViewModel

@Composable
fun TopBar(
    title: String,
    accentColor: Color = Color(0xFF3F4A7A),
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    coinViewModel: CoinViewModel = viewModel(),
    onSettingsClick: (() -> Unit)? = null,
    onCoinBalanceClick: (() -> Unit)? = null
) {
    val coinBalance by coinViewModel.balance.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(
                onClick = {
                    SoundManager.playPop()
                    onBack()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "back",
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Spacer(Modifier.size(8.dp))
        }
        if (onSettingsClick != null) {
            IconButton(
                onClick = {
                    SoundManager.playPop()
                    onSettingsClick()
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "settings",
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
        }

        if (title.isNotBlank()) {
            Text(
                text = title,
                color = accentColor,
                fontSize = 21.sp,
                fontFamily = FontFamily(Font(R.font.poppins_extrabold)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
        } else {
            Spacer(Modifier.weight(1f))
        }



        CoinBalancePill(balance = coinBalance, onClick = onCoinBalanceClick)
    }
}

@Composable
private fun CoinBalancePill(balance: Int, onClick: (() -> Unit)?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(1.5.dp, Color(0xFFFFC93C), RoundedCornerShape(50))
            .let { base ->
                if (onClick != null) {
                    base.clickable {
                        SoundManager.playPop()
                        onClick()
                    }
                } else {
                    base
                }
            }
            .padding(start = 6.dp, end = 12.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFFFE066), Color(0xFFFFB300)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "C",
                color = Color(0xFF8A5A00),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = balance.toString(),
            color = Color(0xFF5C3D00),
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.poppins_extrabold))
        )
    }
}