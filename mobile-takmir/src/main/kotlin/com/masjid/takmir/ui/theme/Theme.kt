package com.masjid.takmir.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TakmirGreen,
    onPrimary = Color.White,
    primaryContainer = TakmirGreenDark,
    secondary = TakmirTeal,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onBackground = OnSurfaceDark,
    error = ExpenseRed,
)

private val LightColorScheme = lightColorScheme(
    primary = TakmirGreen,
    onPrimary = Color.White,
    primaryContainer = TakmirGreenLight,
    secondary = TakmirTeal,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onBackground = OnSurfaceLight,
    error = ExpenseRed,
)

@Composable
fun TakmirTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TakmirTypography,
        content = content
    )
}
