package com.example.reacht_android

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reacht_android.ui.screens.Chats
import com.example.reacht_android.ui.screens.CreateOffer
import com.example.reacht_android.ui.screens.Screen
import com.example.reacht_android.ui.screens.Feed
import com.example.reacht_android.ui.screens.People
import com.example.reacht_android.ui.screens.Profile

@Composable
fun Navigation(navController: NavHostController) {
    val viewModel = ViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {
        composable(Screen.Feed.route) {
            Feed(navController, viewModel)
        }
        composable(Screen.Chats.route) {
            Chats(navController, viewModel)
        }
        composable(Screen.People.route) {
            People(navController, viewModel)
        }
        composable(Screen.Profile.route) {
            Profile(navController, viewModel)
        }
        composable(Screen.CreateOffer.route) {
            CreateOffer(navController, viewModel)
        }
    }
}