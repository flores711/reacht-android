package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.AuthState
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.OffWhite
import com.example.reacht_android.ui.theme.reachtTextFieldColors

@Composable
fun Signup(navController: NavController, viewModel: AppViewModel) {
    var emailInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Se ejecuta cada vez que authState cambia. Al detectar SignupSuccess vuelve a Login
    LaunchedEffect(authState) {
        if (authState is AuthState.SignupSuccess) {
            // No navega hacia delante, sino que coge esa ruta de la pila y vuelve ahí
            // inclusive = true: elimina también Login de la pila antes de meter el nuevo, evitando duplicados
            navController.navigate(Screen.Login.route) {
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
        Text("Create an account", style = MaterialTheme.typography.headlineLarge, color = Blurple)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = reachtTextFieldColors()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = reachtTextFieldColors()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = reachtTextFieldColors()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.signup(emailInput, usernameInput, passwordInput) },
            colors = ButtonDefaults.buttonColors(containerColor = Blurple),
            enabled = authState !is AuthState.Loading,
            shape = RoundedCornerShape(6.dp),
            // TODO: Por qué el padding se pone así
            // TODO: Buscar todos estos y quitarlos si no hacen falta
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp
            )
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
            Text("Already have an account? ", color = OffWhite)
            Text(
                text = "Log in",
                color = Blurple,
                modifier = Modifier.clickable {
                    viewModel.resetAuthState()
                    navController.popBackStack()
                }
            )
        }
    }
}
