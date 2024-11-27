package com.example.kola_language_learning.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kola_language_learning.ui.screens.chat.ChatScreen
import com.example.kola_language_learning.ui.screens.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
//        composable(Screen.Auth.route) {
//            AuthScreen(
//                onNavigateToHome = { navController.navigate(Screen.Home.route) }
//            )
//        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

//        composable(Screen.Settings.route) {
//            SettingsScreen(
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
    }
}