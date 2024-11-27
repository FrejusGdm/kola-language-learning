package com.example.kola_language_learning.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

//val Purple80 = Color(0xFFD0BCFF)
//val PurpleGrey80 = Color(0xFFCCC2DC)
//val Pink80 = Color(0xFFEFB8C8)
//
//val Purple40 = Color(0xFF6650a4)
//val PurpleGrey40 = Color(0xFF625b71)
//val Pink40 = Color(0xFF7D5260)

// Color.kt
val DarkBackground = Color(0xFF121212)
val MintGreen = Color(0xFF4AE1B8)
val SoftMint = Color(0xFF7FFFD4)
val DarkSurface = Color(0xFF1E1E1E)
val TextWhite = Color(0xFFF5F5F5)
val TextGrey = Color(0xFF9E9E9E)

val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    secondary = SoftMint,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onPrimary = DarkBackground
)