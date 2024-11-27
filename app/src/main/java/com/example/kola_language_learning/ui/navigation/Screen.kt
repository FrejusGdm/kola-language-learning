package com.example.kola_language_learning.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Settings : Screen("settings")
}