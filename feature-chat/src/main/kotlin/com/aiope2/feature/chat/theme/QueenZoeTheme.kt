package com.queenzoe.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * QueenZoe Brand Palette (Kelivo-compatible overlay)
 * No structural UI changes — only token substitution layer.
 */
object QueenZoeTheme {

    // =========================
    // CORE BRAND COLORS
    // =========================

    val PrimaryLight = Color(0xFF4D5C92)     // calm royal indigo
    val PrimaryDark = Color(0xFFB6C4FF)      // soft luminous blue

    val SurfaceLight = Color(0xFFF7F7F7)
    val SurfaceDark = Color(0xFF121213)

    val OnSurfaceLight = Color(0xFF202020)
    val OnSurfaceDark = Color(0xFFF9F9F9)

    // =========================
    // ACCENT SYSTEM (QUEENZOE IDENTITY)
    // =========================

    val AccentGold = Color(0xFFD6B25E)
    val AccentRose = Color(0xFFB76E79)
    val AccentViolet = Color(0xFF7C6FAF)

    // =========================
    // UI STATES
    // =========================

    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFB74D)
    val Error = Color(0xFFE57373)

    // =========================
    // BUBBLE TINTING
    // =========================

    val UserBubbleLight = PrimaryLight.copy(alpha = 0.08f)
    val UserBubbleDark = PrimaryDark.copy(alpha = 0.15f)

    val FocusGlow = PrimaryDark.copy(alpha = 0.45f)

    // =========================
    // BACKDROP SYSTEM
    // =========================

    val FrostDark = Color(0xFF1C1C1E).copy(alpha = 0.66f)
    val FrostLight = Color.White.copy(alpha = 0.66f)
}