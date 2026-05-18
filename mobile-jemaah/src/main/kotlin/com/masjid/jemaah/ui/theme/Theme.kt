package com.masjid.jemaah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = JemaahBlue,
    onPrimary = Color.White,
    primaryContainer = JemaahBlueDark,
    secondary = JemaahIndigo,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onBackground = OnSurfaceDark,
    error = ExpenseRed,
)

private val LightColorScheme = lightColorScheme(
    primary = JemaahBlue,
    onPrimary = Color.White,
    primaryContainer = JemaahBlueLight,
    secondary = JemaahIndigo,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onBackground = OnSurfaceLight,
    error = ExpenseRed,
)

@Composable
fun JemaahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JemaahTypography,
        content = content
    )
}
