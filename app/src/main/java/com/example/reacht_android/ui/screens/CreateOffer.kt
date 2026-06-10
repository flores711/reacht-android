package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.CreateOfferState
import com.example.reacht_android.model.Chat
import com.example.reacht_android.model.Videogame
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.ErrorRed
import com.example.reacht_android.ui.theme.MediumGrey
import com.example.reacht_android.ui.theme.OffWhite
import com.example.reacht_android.ui.theme.reachtTextFieldColors

@Composable
fun CreateOffer(navController: NavController, viewModel: AppViewModel) {
    val createOfferState by viewModel.createOfferState.collectAsState()
    val videogames by viewModel.videogames.collectAsState()

    var videogameSearch by remember { mutableStateOf("") }
    var selectedVideogame by remember { mutableStateOf<Videogame?>(null) }
    var description by remember { mutableStateOf("") }
    var targetPlayers by remember { mutableStateOf("") }

    var filteredGames = emptyList<Videogame>()
    if (videogameSearch.isBlank()) {
        filteredGames = emptyList()
    } else {
        filteredGames = videogames.filter { game ->
            game.title.contains(videogameSearch, ignoreCase = true)
        }.take(5)   // Solo cogemos max los 5 primeros de los resultados filtrados
    }

    LaunchedEffect(Unit) {
        if (videogames.isEmpty()) viewModel.loadVideogames()
    }

    LaunchedEffect(createOfferState) {
        val state = createOfferState
        if (state is CreateOfferState.Success) {
            viewModel.selectChat(Chat(chatId = state.chatId, name = state.chatName))
            viewModel.resetCreateOfferState()
            navController.navigate(Screen.SingleChat.route)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetCreateOfferState();
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrey)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // IconButton tiene 12dp de padding interno en Material3; el offset compensa
        // para que el icono quede alineado visualmente con el texto de abajo
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Create Offer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "VIDEOGAME",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OffWhite,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = videogameSearch,
            onValueChange = { newValue ->
                videogameSearch = newValue
                selectedVideogame = null
            },
            label = { Text("Videogame") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = reachtTextFieldColors()
        )
        if (filteredGames.isNotEmpty() && selectedVideogame == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MediumGrey)
            ) {
                Column {
                    filteredGames.forEach { game ->
                        TextButton(
                            onClick = {
                                selectedVideogame = game
                                videogameSearch = game.title
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = game.title,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "DESCRIPTION",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OffWhite,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { newValue -> description = newValue },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = reachtTextFieldColors()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TARGET PLAYERS",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OffWhite,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = targetPlayers,
            onValueChange = { newValue ->
                val newValueInt = newValue.toIntOrNull()
                // Dejamos si esta vacio, por ej para cambiar de número de 5 a 20 (tiene que borrar todo primero)ç
                if (newValue.isEmpty() || (newValueInt != null && newValueInt <= 100)) {
                    targetPlayers = newValue
                }
            },
            label = { Text("Target players") },
            placeholder = { Text("min. 2  |  max. 100") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // Para que en android salga solo el teclado numérico y no el normal
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = reachtTextFieldColors()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.createOffer(
                    description = description,
                    targetPlayers = targetPlayers.toInt(),
                    videogameId = selectedVideogame!!.id
                )
            },
            // Esto para que no se pueda enviar nada si no están esos campos llenos, pero se permite que no haya descripción
            enabled = selectedVideogame != null && targetPlayers.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Blurple),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Create",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        if (createOfferState is CreateOfferState.Error) {
            Text(
                text = (createOfferState as CreateOfferState.Error).message,
                color = ErrorRed,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
