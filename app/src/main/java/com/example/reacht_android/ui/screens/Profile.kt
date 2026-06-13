package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.UpdateUserState
import com.example.reacht_android.ui.OfferCard
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.ErrorRed
import com.example.reacht_android.ui.theme.MediumGrey
import com.example.reacht_android.ui.theme.OffWhite
import com.example.reacht_android.ui.theme.reachtTextFieldColors
import com.example.reacht_android.ui.theme.SuccessGreen
import com.example.reacht_android.ui.theme.VividRed

@Composable
fun Profile(navController: NavController, viewModel: AppViewModel) {
    val currentOffer by viewModel.currentOffer.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val updateUserState by viewModel.updateUserState.collectAsState()

    var usernameField by remember { mutableStateOf(viewModel.username) }
    var emailField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentOffer()
        viewModel.loadUserData()
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) emailField = userEmail
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrey)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "@${viewModel.username}",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACTIVE OFFER",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OffWhite,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (currentOffer == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MediumGrey)
            ) {
                Text(
                    text = "No active offer",
                    color = OffWhite,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            val isCreator = currentOffer!!.creatorId == viewModel.userId
            OfferCard(
                offer = currentOffer!!,
                onOfferClick = {},
                onOfferJoin = {},
                isJoined = true,
                isCreator = isCreator,
                onOfferLeave = {
                    if (isCreator) viewModel.deleteOffer(currentOffer!!.offerId)
                    else viewModel.leaveOffer(currentOffer!!.offerId)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "ACCOUNT",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OffWhite,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MediumGrey)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = usernameField,
                    onValueChange = { usernameField = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = reachtTextFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = emailField,
                    onValueChange = { emailField = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = reachtTextFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = passwordField,
                    onValueChange = { passwordField = it },
                    label = { Text("New password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = reachtTextFieldColors()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.resetUpdateUserState()
                        viewModel.updateUser(
                            newUsername = usernameField,
                            newEmail = emailField,
                            newPassword = passwordField.ifBlank { null }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Blurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Save changes",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                when (val state = updateUserState) {
                    is UpdateUserState.Success -> Text(
                        text = "Saved!",
                        color = SuccessGreen,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    is UpdateUserState.Error -> Text(
                        text = state.message,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    else -> {}
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.logOut()
                        navController.navigate(Screen.Login.route)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VividRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Log out",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
