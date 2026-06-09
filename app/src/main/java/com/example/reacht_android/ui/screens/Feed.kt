package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.JoinOfferState
import com.example.reacht_android.model.Chat
import com.example.reacht_android.model.Videogame
import com.example.reacht_android.ui.OfferCard
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.BrightGrey
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.LightGrey
import com.example.reacht_android.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Feed(navController: NavController, viewModel: AppViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val joinOfferState by viewModel.joinOfferState.collectAsState()
    val videogames by viewModel.videogames.collectAsState()

    var filtersExpanded by remember { mutableStateOf(false) }
    var gameSearchText by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<Videogame?>(null) }
    var categoriesExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var minCurrentPlayers by remember { mutableStateOf("") }
    var maxTargetPlayers by remember { mutableStateOf("") }

    var filteredGames = emptyList<Videogame>()
    if (gameSearchText.isBlank()) {
        filteredGames = emptyList()
    } else {
        filteredGames = videogames.filter { game ->
            game.title.contains(gameSearchText, ignoreCase = true)
        }.take(5)
    }

    val categories = videogames.map { it.category }.distinct().sorted()

    LaunchedEffect(Unit) {
        viewModel.searchOffers()
        if (videogames.isEmpty()) viewModel.loadVideogames()
    }

    LaunchedEffect(joinOfferState) {
        val state = joinOfferState
        when (state) {
            is JoinOfferState.Success -> {
                viewModel.selectChat(Chat(chatId = state.chatId, name = state.chatName))
                viewModel.resetJoinOfferState()
                navController.navigate(Screen.SingleChat.route)
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGrey),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightGrey)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Fila de cuadro búsqueda videojuego y boton para expandir filtros
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 8.dp),
                        value = gameSearchText,
                        onValueChange = { newValue ->
                            gameSearchText = newValue
                            selectedGame = null // Si escribimos se deselecciona el que hubiera y se vuelven a abrir las opciones
                        },
                        label = { Text("Videogame") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blurple,
                            unfocusedBorderColor = Color(0xFF3A3A3A),
                            focusedLabelColor = Blurple,
                            unfocusedLabelColor = OffWhite,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    // Botón de buscar
                    IconButton(
                        onClick = {
                            viewModel.searchOffers(
                                videogameId = selectedGame?.id,
                                category = selectedCategory,
                                minPlayers = minCurrentPlayers.ifBlank { null },
                                maxPlayers = maxTargetPlayers.ifBlank { null }
                            )
                        },
                        modifier = Modifier
                            // TODO: Por qué se pone el color y round corners junto en background
                            // En OfferCard por ejemplo está en shape = RoundedCornerShape(6.dp)
                            .background(Blurple, RoundedCornerShape(8.dp))
                            .size(54.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { filtersExpanded = !filtersExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Toggle filters",
                            // Detalle de si se ha tocado algo de los filtros se pone en morado
                            tint = if (filtersExpanded || selectedGame != null || selectedCategory != null
                                || minCurrentPlayers.isNotBlank() || maxTargetPlayers.isNotBlank()
                            ) Blurple else OffWhite
                        )
                    }
                }

                // Card de lista de juegos conforme vamos escribiendo
                // Comprueba que los juegos se hayan cargado y que no haya ya uno seleccionado
                if (filteredGames.isNotEmpty() && selectedGame == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
                    ) {
                        Column {
                            filteredGames.forEach { game ->
                                TextButton(
                                    onClick = {
                                        selectedGame = game
                                        gameSearchText = game.title
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

                // Menú extenso de filtros
                if (filtersExpanded) {
                    Column {
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "CATEGORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OffWhite,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        // Filtro categorías
                        // COmbina un text field con un menu desplegable para ver lo elegido
                        ExposedDropdownMenuBox(
                            expanded = categoriesExpanded,
                            onExpandedChange = { categoriesExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory ?: "All categories",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriesExpanded)
                                },
                                // El menuanchor() es lo que lo relaciona con exposeddropdownmenubox
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Blurple,
                                    unfocusedBorderColor = Color(0xFF3A3A3A),
                                    focusedLabelColor = Blurple,
                                    unfocusedLabelColor = OffWhite,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = OffWhite,
                                    focusedTrailingIconColor = Blurple,
                                    unfocusedTrailingIconColor = OffWhite
                                )
                            )
                            // Y esto es realmente lo que se despliega
                            ExposedDropdownMenu(
                                expanded = categoriesExpanded,
                                onDismissRequest = { categoriesExpanded = false }
                            ) {
                                // Ponemos una primera categoría a mano que es todas las categorías,
                                // y si se selecciona simplemente selectedCategory se pone a null
                                DropdownMenuItem(
                                    text = { Text("All categories", color = OffWhite) },
                                    onClick = {
                                        selectedCategory = null
                                        categoriesExpanded = false
                                    }
                                )
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category, color = OffWhite) },
                                        onClick = {
                                            selectedCategory = category
                                            categoriesExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MIN CURRENT PLAYERS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OffWhite,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = minCurrentPlayers,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                                            minCurrentPlayers = newValue
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Blurple,
                                        unfocusedBorderColor = Color(0xFF3A3A3A),
                                        focusedLabelColor = Blurple,
                                        unfocusedLabelColor = OffWhite,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MAX TARGET PLAYERS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OffWhite,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = maxTargetPlayers,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                                            maxTargetPlayers = newValue
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Blurple,
                                        unfocusedBorderColor = Color(0xFF3A3A3A),
                                        focusedLabelColor = Blurple,
                                        unfocusedLabelColor = OffWhite,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                        }

                    }
                }
            }

            // Mostrar cards ofertas o cargando
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Blurple)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    contentPadding = PaddingValues(bottom = 10.dp)
                ) {
                    items(offers) { offer ->
                        Spacer(Modifier.padding(top = 10.dp))
                        OfferCard(
                            offer = offer,
                            onOfferClick = {
                                viewModel.selectOffer(offer)
                                navController.navigate(Screen.OfferDetail.route)
                            },
                            onOfferJoin = { viewModel.joinOffer(offer.offerId) }
                        )
                    }
                }
            }
        }

        // Botón de crear oferta abajo a la derecha
        FloatingActionButton(
            onClick = { navController.navigate(Screen.CreateOffer.route) },
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.BottomCenter)
                .border(4.dp, Blurple, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(10.dp),
            containerColor = BrightGrey,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 16.dp)
        ) {
            Icon(
                modifier = Modifier.padding(20.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = "Create Offer",
                tint = Color.White,
            )
        }
    }
}
