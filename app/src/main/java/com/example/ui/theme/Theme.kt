package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameDarkColorScheme = darkColorScheme(
    primary = HoneyGold,
    onPrimary = Color.Black,
    primaryContainer = HoneyDeep,
    onPrimaryContainer = Color.White,
    secondary = HoneyAmber,
    onSecondary = Color.Black,
    tertiary = SoftCyan,
    background = GameDarkBg,
    onBackground = HoneyCream,
    surface = GameSurfaceDark,
    onSurface = HoneyCream,
    surfaceVariant = GameSurfaceElevated,
    onSurfaceVariant = HoneyCream,
    outline = GameBorder
)

@Composable
fun BeeSwarmTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameDarkColorScheme,
        typography = Typography,
        content = content
    )
}
