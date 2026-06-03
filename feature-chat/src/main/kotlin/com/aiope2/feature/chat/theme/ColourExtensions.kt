package com.queenzoe.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun qzPrimary(): Color =
    if (isSystemInDarkTheme()) QueenZoeTheme.PrimaryDark
    else QueenZoeTheme.PrimaryLight

@Composable
fun qzSurface(): Color =
    if (isSystemInDarkTheme()) QueenZoeTheme.SurfaceDark
    else QueenZoeTheme.SurfaceLight

@Composable
fun qzOnSurface(): Color =
    if (isSystemInDarkTheme()) QueenZoeTheme.OnSurfaceDark
    else QueenZoeTheme.OnSurfaceLight

@Composable
fun qzUserBubble(): Color =
    if (isSystemInDarkTheme())
        QueenZoeTheme.UserBubbleDark
    else
        QueenZoeTheme.UserBubbleLight