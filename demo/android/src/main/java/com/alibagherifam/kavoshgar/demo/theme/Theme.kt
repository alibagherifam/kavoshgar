package com.alibagherifam.kavoshgar.demo.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppLightColorScheme = lightColorScheme(
    primary = AppLightColor.primary,
    onPrimary = AppLightColor.onPrimary,
    primaryContainer = AppLightColor.primaryContainer,
    onPrimaryContainer = AppLightColor.onPrimaryContainer,
    secondary = AppLightColor.secondary,
    onSecondary = AppLightColor.onSecondary,
    secondaryContainer = AppLightColor.secondaryContainer,
    onSecondaryContainer = AppLightColor.onSecondaryContainer,
    tertiary = AppLightColor.tertiary,
    onTertiary = AppLightColor.onTertiary,
    tertiaryContainer = AppLightColor.tertiaryContainer,
    onTertiaryContainer = AppLightColor.onTertiaryContainer,
    error = AppLightColor.error,
    errorContainer = AppLightColor.errorContainer,
    onError = AppLightColor.onError,
    onErrorContainer = AppLightColor.onErrorContainer,
    background = AppLightColor.background,
    onBackground = AppLightColor.onBackground,
    surface = AppLightColor.surface,
    onSurface = AppLightColor.onSurface,
    surfaceVariant = AppLightColor.surfaceVariant,
    onSurfaceVariant = AppLightColor.onSurfaceVariant,
    outline = AppLightColor.outline,
    inverseOnSurface = AppLightColor.inverseOnSurface,
    inverseSurface = AppLightColor.inverseSurface,
    inversePrimary = AppLightColor.inversePrimary,
    surfaceTint = AppLightColor.surfaceTint,
    outlineVariant = AppLightColor.outlineVariant,
    scrim = AppLightColor.scrim,
)

private val AppDarkColorScheme = darkColorScheme(
    primary = AppDarkColor.primary,
    onPrimary = AppDarkColor.onPrimary,
    primaryContainer = AppDarkColor.primaryContainer,
    onPrimaryContainer = AppDarkColor.onPrimaryContainer,
    secondary = AppDarkColor.secondary,
    onSecondary = AppDarkColor.onSecondary,
    secondaryContainer = AppDarkColor.secondaryContainer,
    onSecondaryContainer = AppDarkColor.onSecondaryContainer,
    tertiary = AppDarkColor.tertiary,
    onTertiary = AppDarkColor.onTertiary,
    tertiaryContainer = AppDarkColor.tertiaryContainer,
    onTertiaryContainer = AppDarkColor.onTertiaryContainer,
    error = AppDarkColor.error,
    errorContainer = AppDarkColor.errorContainer,
    onError = AppDarkColor.onError,
    onErrorContainer = AppDarkColor.onErrorContainer,
    background = AppDarkColor.background,
    onBackground = AppDarkColor.onBackground,
    surface = AppDarkColor.surface,
    onSurface = AppDarkColor.onSurface,
    surfaceVariant = AppDarkColor.surfaceVariant,
    onSurfaceVariant = AppDarkColor.onSurfaceVariant,
    outline = AppDarkColor.outline,
    inverseOnSurface = AppDarkColor.inverseOnSurface,
    inverseSurface = AppDarkColor.inverseSurface,
    inversePrimary = AppDarkColor.inversePrimary,
    surfaceTint = AppDarkColor.surfaceTint,
    outlineVariant = AppDarkColor.outlineVariant,
    scrim = AppDarkColor.scrim,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColorScheme = if (!useDarkTheme) {
        AppLightColorScheme
    } else {
        AppDarkColorScheme
    }

    MaterialTheme(
        appColorScheme,
        AppShapes,
        AppTypography,
        content
    )
}
