package com.queenzoe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val DarkColors = darkColorScheme(
    primary = QueenZoeTheme.PrimaryDark,
    background = QueenZoeTheme.SurfaceDark,
    surface = QueenZoeTheme.SurfaceDark,
    onSurface = QueenZoeTheme.OnSurfaceDark
)

private val LightColors = lightColorScheme(
    primary = QueenZoeTheme.PrimaryLight,
    background = QueenZoeTheme.SurfaceLight,
    surface = QueenZoeTheme.SurfaceLight,
    onSurface = QueenZoeTheme.OnSurfaceLight
)

@Composable
fun QueenZoeAppTheme(
    content: @Composable () -> Unit
) {
    val dark = isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}