package com.example.reacht_android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val route: String,
    val defaultIcon: ImageVector,
    val selectedIcon: ImageVector,
    val hasNews: Boolean? = null,
    // val notificationCount: Int? = null
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(
        route = "feed",
        defaultIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    BottomNavigationItem(
        route = "chats",
        defaultIcon = Icons.Outlined.Email,
        selectedIcon = Icons.Filled.Email
    ),
BottomNavigationItem(
        route = "profile",
        defaultIcon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle
    )
)