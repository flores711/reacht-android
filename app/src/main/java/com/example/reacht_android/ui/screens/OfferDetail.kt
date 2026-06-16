package com.example.reacht_android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.reacht_android.AppViewModel
import com.example.reacht_android.JoinOfferState
import com.example.reacht_android.model.Chat
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.DarkGrey
import com.example.reacht_android.ui.theme.MediumGrey
import com.example.reacht_android.ui.theme.LightGrey
import com.example.reacht_android.ui.theme.OffWhite
import kotlin.math.roundToInt

@Composable
fun OfferDetail(navController: NavController, viewModel: AppViewModel) {
    val offer by viewModel.selectedOffer.collectAsState()
    val currentOffer = offer ?: return
    val joinOfferState by viewModel.joinOfferState.collectAsState()

    LaunchedEffect(joinOfferState) {
        val state = joinOfferState
        when (state) {
            is JoinOfferState.Success -> {
                viewModel.selectChat(Chat(chatId = state.chatId, name = state.chatName))
                viewModel.resetJoinOfferState()
                navController.navigate(Screen.SingleChat.route)
            }
            is JoinOfferState.Error -> {
                // Para que el mensaje de error se muestre 3 segundos y luego desaparezca (reset state)
                delay(3000)
                viewModel.resetJoinOfferState()
            }
            else -> {}
        }
    }

    val slots = minOf(currentOffer.targetPlayers, 10)
    val filledSlots = if (currentOffer.targetPlayers <= 10) {
        currentOffer.currentPlayers
    } else {
        (currentOffer.currentPlayers.toFloat() / currentOffer.targetPlayers * slots).roundToInt()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MediumGrey)
                    .padding(horizontal = 26.dp, vertical = 20.dp)
            ) {
                Column {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        // IconButton pone padding automaticamente a la izq
                        // Con esto lo echamos a la izquierda otra vez, para que este alineado
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentOffer.videogame.title,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "@${currentOffer.creatorUsername}",
                        fontSize = 16.sp,
                        color = OffWhite
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MediumGrey)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PLAYERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OffWhite,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // Tope en 10 círculos, 10 target players. A partir de ahí es proporción
                        // Lógica al principio de esta clase
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(slots) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index < filledSlots) Blurple
                                                else LightGrey
                                            )
                                    )
                                }
                            }
                            Text(
                                text = "${currentOffer.currentPlayers} / ${currentOffer.targetPlayers}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                if (currentOffer.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MediumGrey)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "DESCRIPTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OffWhite,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentOffer.description,
                                fontSize = 17.sp,
                                color = Color.White,
                                lineHeight = 26.sp
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.joinOffer(currentOffer.offerId) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blurple),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Join",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Mensaje de error de 3seg como en feed
        AnimatedVisibility(
            visible = joinOfferState is JoinOfferState.Error,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 24.dp, end = 24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MediumGrey),
            ) {
                Text(
                    text = (joinOfferState as? JoinOfferState.Error)?.message ?: "",
                    color = OffWhite,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}
