package com.example.reacht_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.reacht_android.ui.screens.Screen
import com.example.reacht_android.ui.theme.ReachtandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReachtandroidTheme(darkTheme = true) {
                val navController = rememberNavController()
                var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val showBottomBar = currentRoute !in listOf(Screen.Login.route, Screen.Signup.route)

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) NavigationBar {
                            bottomNavigationItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        selectedItemIndex = index
                                        navController.navigate(item.route)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedItemIndex == index) item.selectedIcon else item.defaultIcon,
                                            contentDescription = item.route
                                        )
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Navigation(navController)
                    }
                }
            }
        }
    }
}
