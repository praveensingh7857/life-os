package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LifeOsColorScheme = darkColorScheme(
    primary = LifeOsAccentGold,
    onPrimary = LifeOsBgPrimary,
    primaryContainer = LifeOsElevated,
    onPrimaryContainer = LifeOsAccentGoldLight,
    secondary = LifeOsAccentGoldLight,
    onSecondary = LifeOsBgPrimary,
    secondaryContainer = LifeOsCardBg,
    onSecondaryContainer = LifeOsTextPrimary,
    tertiary = LifeOsInfo,
    onTertiary = LifeOsBgPrimary,
    background = LifeOsBgPrimary,
    onBackground = LifeOsTextPrimary,
    surface = LifeOsCardBg,
    onSurface = LifeOsTextPrimary,
    surfaceVariant = LifeOsElevated,
    onSurfaceVariant = LifeOsTextSecondary,
    outline = LifeOsBorder,
    outlineVariant = LifeOsBorderGold,
    error = LifeOsAlert,
    onError = Color.White
)

@Composable
fun LifeOsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LifeOsColorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias for template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LifeOsTheme(content = content)
}
