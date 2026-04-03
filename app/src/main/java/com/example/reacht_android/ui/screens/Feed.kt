package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reacht_android.ViewModel
import com.example.reacht_android.ui.theme.LightGrey

@Composable
fun Feed(navController: NavController, viewModel: ViewModel) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(100.dp)
                .background(LightGrey),
            contentAlignment = Alignment.Center
        ) {
            /* Barra de búsqueda, donde se pueda escribir y de donde se pueda coger el texto
            // para pasárselo al ViewModel y que él pueda procesarlo, consultando a la base de datos
            // y devolviendo los resultados correspondientes
            // Botón de filtros con ventana de filtros, y lo mismo, que lo que se pulse lo coja el viewmodel
            // (pero esto más adelante) */

            TextField(
                value = searchText,
                onValueChange = { newValue ->
                    viewModel.onSearchQueryChanged(newValue)
                }
                /* Se puede sustituir la lambda por viewModel::onSearchQueryChanged
                // Un shortcut que se puede utilizar cuando la función que pasamos recibe y devuelve
                // exactamente lo que onValueChange espera, recibe String y devuelve Unit
                // Le pasará automáticamente el valor nuevo */
            )
        }
    }
}