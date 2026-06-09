package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.AuthState
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.OffWhite

@Composable
fun Login(navController: NavController, viewModel: AppViewModel) {
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Se ejecuta sólo cuando la key (authState) cambia.
    LaunchedEffect(authState) {
        if (authState is AuthState.LoginSuccess) {
            navController.navigate(Screen.Feed.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.resetAuthState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrey)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reacht", style = MaterialTheme.typography.headlineLarge, color = Blurple)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(usernameInput, passwordInput) },
            colors = ButtonDefaults.buttonColors(containerColor = Blurple),
            enabled = authState !is AuthState.Loading
        ) {
            Text("Log In")
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("Don't have an account? ", color = OffWhite)
            Text(
                text = "Sign up",
                color = Blurple,
                modifier = Modifier.clickable {
                    viewModel.resetAuthState()
                    navController.navigate(Screen.Signup.route)
                }
            )
        }
    }
}
