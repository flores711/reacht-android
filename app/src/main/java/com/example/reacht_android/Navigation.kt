package com.example.reacht_android

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reacht_android.ui.screens.Chats
import com.example.reacht_android.ui.screens.CreateOffer
import com.example.reacht_android.ui.screens.Feed
import com.example.reacht_android.ui.screens.Login
import com.example.reacht_android.ui.screens.Profile
import com.example.reacht_android.ui.screens.Screen
import com.example.reacht_android.ui.screens.OfferDetail
import com.example.reacht_android.ui.screens.SingleChat
import com.example.reacht_android.ui.screens.Signup

@Composable
fun Navigation(navController: NavHostController) {
    val viewModel: AppViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            Login(navController, viewModel)
        }
        composable(Screen.Signup.route) {
            Signup(navController, viewModel)
        }
        composable(Screen.Feed.route) {
            Feed(navController, viewModel)
        }
        composable(Screen.Chats.route) {
            Chats(navController, viewModel)
        }
composable(Screen.Profile.route) {
            Profile(navController, viewModel)
        }
        composable(Screen.CreateOffer.route) {
            CreateOffer(navController, viewModel)
        }
        composable(Screen.OfferDetail.route) {
            OfferDetail(navController, viewModel)
        }
        composable(Screen.SingleChat.route) {
            SingleChat(navController, viewModel)
        }
    }
}
