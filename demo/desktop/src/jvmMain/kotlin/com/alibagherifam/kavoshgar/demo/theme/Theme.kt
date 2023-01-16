package com.alibagherifam.kavoshgar.demo.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = DarkGreen,
    primaryVariant = Olive,
    onPrimary = Color.White,
    secondary = Lemon,
    secondaryVariant = LightGreen,
    onSecondary = DarkGreen,
    surface = Silver,
    onSurface = Black,
    background = Color.White,
    onBackground = Black,
    error = Red400,
    onError = Color.White
)

private val DarkColorPalette = LightColorPalette

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
