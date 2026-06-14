package com.example.a210122_nazatul_lab1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//
private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen,
    onPrimary = DarkBackground,
    primaryContainer = Color(0xFF145C31),
    onPrimaryContainer = Color(0xFFB8F5CD),
    secondary = TextSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = TextOnDark,
    tertiary = BrandGreenAccent,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextOnDark,
    surface = DarkSurface,
    onSurface = TextOnDark,
    surfaceVariant = Color(0xFF323232),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF4D4D4D),
    outlineVariant = Color(0xFF3A3A3A)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = TextOnDark,
    primaryContainer = Color(0xFFD7F7E3),
    onPrimaryContainer = Color(0xFF00210F),
    secondary = Color(0xFF5F6368),
    onSecondary = TextOnDark,
    secondaryContainer = Color(0xFFF1F1F1),
    onSecondaryContainer = AppLightText,
    tertiary = BrandGreenAccent,
    onTertiary = TextOnDark,
    background = AppLightBackground,
    onBackground = AppLightText,
    surface = AppLightSurface,
    onSurface = AppLightText,
    surfaceVariant = AppLightSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AppLightOutline,
    outlineVariant = Color(0xFFE3E3E3)
)

@Composable
fun A210122_NAZATUL_Lab1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}