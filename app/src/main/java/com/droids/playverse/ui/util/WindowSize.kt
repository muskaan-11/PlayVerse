package com.droids.playverse.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScreenSize {
    PHONE,
    SMALL_TABLET,
    LARGE_TABLET
}

@Immutable
data class WindowInfo(
    val screenSize: ScreenSize,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp
)

@Composable
fun rememberWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val screenSize = when {
        configuration.screenWidthDp < 600 -> ScreenSize.PHONE
        configuration.screenWidthDp < 840 -> ScreenSize.SMALL_TABLET
        else -> ScreenSize.LARGE_TABLET
    }
    return WindowInfo(
        screenSize = screenSize,
        screenWidthDp = configuration.screenWidthDp.dp,
        screenHeightDp = configuration.screenHeightDp.dp
    )
}

fun WindowInfo.scaledDp(phone: Dp, smallTablet: Dp = phone, largeTablet: Dp = smallTablet): Dp =
    when (screenSize) {
        ScreenSize.PHONE -> phone
        ScreenSize.SMALL_TABLET -> smallTablet
        ScreenSize.LARGE_TABLET -> largeTablet
    }

fun WindowInfo.scaledSp(phone: TextUnit, smallTablet: TextUnit = phone, largeTablet: TextUnit = smallTablet): TextUnit =
    when (screenSize) {
        ScreenSize.PHONE -> phone
        ScreenSize.SMALL_TABLET -> smallTablet
        ScreenSize.LARGE_TABLET -> largeTablet
    }

fun WindowInfo.contentMaxWidth(): Dp =
    when (screenSize) {
        ScreenSize.PHONE -> screenWidthDp
        ScreenSize.SMALL_TABLET -> 480.dp
        ScreenSize.LARGE_TABLET -> 560.dp
    }

val WindowInfo.isTablet: Boolean
    get() = screenSize != ScreenSize.PHONE