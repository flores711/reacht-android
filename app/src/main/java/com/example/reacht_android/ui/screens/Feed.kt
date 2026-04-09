package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.reacht_android.ViewModel
import com.example.reacht_android.model.Game
import com.example.reacht_android.model.Offer
import com.example.reacht_android.ui.OfferCard
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.LightGrey

@Composable
fun Feed(navController: NavController, viewModel: ViewModel) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGrey),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .background(LightGrey),
                contentAlignment = Alignment.Center
            ) {
                /* Barra de búsqueda, donde se pueda escribir y de donde se pueda coger el texto
                // para pasárselo al ViewModel y que él pueda procesarlo, consultando al servidor y éste a la base de datos
                // y devolviendo los resultados correspondientes
                // Botón de filtros con ventana de filtros, y lo mismo, que lo que se pulse lo coja el viewmodel
                // (pero esto más adelante) */

                /* Se puede sustituir la lambda por viewModel::onSearchQueryChanged
                // Un shortcut que se puede utilizar cuando la función que pasamos recibe y devuelve
                // exactamente lo que onValueChange espera, recibe String y devuelve Unit
                // Le pasará automáticamente el valor nuevo */
                // todo: personalizar barra
                TextField(
                    value = searchText,
                    onValueChange = { newValue: String ->
                        viewModel.onSearchQueryChanged(newValue)
                    },
                    placeholder = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
            }
            LazyColumn() {
                items(searchResults) { offer ->
                    Spacer(Modifier.padding(top = 10.dp))
                    OfferCard(offer)
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate(Screen.CreateOffer.route) },
            modifier = Modifier
                .padding(18.dp, bottom = 150.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Create Offer")
        }
    }
}