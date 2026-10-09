package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RoyalPurpleLight,
    onPrimary = Color.White,
    primaryContainer = RoyalPurplePrimary,
    onPrimaryContainer = Color.White,
    secondary = RoyalGold,
    onSecondary = Color.Black,
    tertiary = QueenRose,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCardSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = RoyalPurpleContainer,
    onPrimaryContainer = RoyalPurpleDark,
    secondary = RoyalGold,
    onSecondary = Color.Black,
    tertiary = QueenRose,
    background = CreamBackground,
    surface = CardBackgroundWhite,
    surfaceVariant = SoftPink,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryMuted
)

@Composable
fun QueenPhoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
