package com.example.reacht_android

import com.example.reacht_android.model.Game
import com.example.reacht_android.model.Offer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModel {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Offer>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun onSearchQueryChanged(newValue: String) {
        _searchText.value = newValue
        getSearchResults()
    }

    private fun getSearchResults() {
        _searchResults.value = listOf(
            Offer(Game("Call Of Duty"), 1, 5),
            Offer(Game("Minecraft"), 1, 2),
            Offer(Game("Nier: Automata"), 2, 3),
        )
        // En esta función se cogerán los parámetros de búsqueda y filtros,
        // se consultará a la base de datos y se devolverá la lista correspondiente
        // de valores o lo que haga falta

        // Supongo que se deberá observar el estado de la barra de búsqueda y ejecutar esto
        // cuando cambie. Añadir además delay para no buscar con cada cambio, sino cuando
        // el usuario haya dejado de escribir hace 500ms o algo así
        // (volver a vídeo de Philipp Lackner de esto más adelante si es necesario)
    }

}