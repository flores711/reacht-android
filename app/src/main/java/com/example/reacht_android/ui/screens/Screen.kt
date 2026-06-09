package com.example.reacht_android.ui.screens

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Signup: Screen("signup")
    object Feed: Screen("feed")
    object Chats: Screen("chats")
    object Profile: Screen("profile")
    object CreateOffer: Screen("create-offer")
    object OfferDetail: Screen("offer-detail")
    object SingleChat: Screen("single-chat")
}