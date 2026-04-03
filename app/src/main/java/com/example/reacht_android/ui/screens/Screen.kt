package com.example.reacht_android.ui.screens

sealed class Screen(val route: String) {
    object Feed: Screen("feed")
    object Chats: Screen("chats")
    object People: Screen("people")
    object Profile: Screen("profile")
}