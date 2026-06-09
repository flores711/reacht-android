package com.example.reacht_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.JoinOfferState
import com.example.reacht_android.model.Chat

import com.example.reacht_android.ui.OfferCard
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.BrightGrey
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.LightGrey
import com.example.reacht_android.ui.theme.OffWhite

@Composable
fun Feed(navController: NavController, viewModel: AppViewModel) {
    val searchText by viewModel.searchText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val joinOfferState by viewModel.joinOfferState.collectAsState()

    // Se ejecuta cada vez que entras en esta pantalla, o sea que siempre está actualizado
    LaunchedEffect(Unit) {
        viewModel.searchOffers()
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
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .background(LightGrey),
                contentAlignment = Alignment.Center
            ) {
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

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Blurple)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 10.dp)) {
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

        FloatingActionButton(
            onClick = { navController.navigate(Screen.CreateOffer.route) },
            modifier = Modifier
                .padding(end = 20.dp, bottom = 20.dp)
                .align(Alignment.BottomEnd)
                .border(4.dp, Blurple, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(10.dp),
            containerColor = BrightGrey,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 16.dp)
        ) {
//            Text(
//                modifier = Modifier.padding(horizontal = 20.dp),
//                text = "Create\noffer",
//                color = OffWhite,
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center
//            )
            Icon(
                modifier = Modifier.padding(20.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = "Create Offer",
                tint = Color.White,
            )
        }
    }
}
