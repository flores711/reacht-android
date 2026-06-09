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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.LightGrey
import com.example.reacht_android.ui.theme.OffWhite

@Composable
fun SingleChat(navController: NavController, viewModel: AppViewModel) {
    val chat = viewModel.selectedChat ?: return
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.enterChat(chat.chatId)
    }

    // Se ejecuta cuando salimos de esta pantalla, y llamamos a exitchat
    DisposableEffect(Unit) {
        onDispose {
            viewModel.exitChat()
        }
    }

    // Cada vez que llega un mensaje nuevo, se hace scroll al último
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrey)
    ) {
        // Cabecera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGrey)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 12.dp)
            )
            Text(
                text = chat.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Lista de mensajes
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                val isMe = message.userId == viewModel.userId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = if (isMe) Blurple else LightGrey,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(0.75f)
                    ) {
                        if (!isMe) {
                            Text(
                                text = message.userUsername,
                                color = Blurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Campo de texto y botón enviar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGrey)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message...", color = OffWhite) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blurple,
                    unfocusedBorderColor = Color(0xFF3A3A3A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(chat.chatId, inputText.trim())
                        inputText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blurple),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Send", color = Color.White)
            }
        }
    }
}
