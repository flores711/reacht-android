package com.example.reacht_android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reacht_android.model.Offer
import com.example.reacht_android.model.Videogame
import com.example.reacht_android.ui.theme.Blurple
import com.example.reacht_android.ui.theme.LightGrey
import com.example.reacht_android.ui.theme.OffWhite
import kotlin.math.roundToInt

@Composable
fun OfferCard(
    offer: Offer,
    onOfferClick: () -> Unit,
    onOfferJoin: () -> Unit,
    isJoined: Boolean = false,
    onOfferLeave: () -> Unit = {}
) {
    val slots = minOf(offer.targetPlayers, 10)
    val filledSlots = if (offer.targetPlayers <= 10) {
        offer.currentPlayers
    } else {
        (offer.currentPlayers.toFloat() / offer.targetPlayers * slots).roundToInt()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onOfferClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGrey,
            contentColor = Color.White
        ),
        // TODO: que es esto
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // TODO: por qué intrisic size
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Ralla morada de la izquierda
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Blurple)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = offer.videogame.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "@${offer.creatorUsername}",
                    fontSize = 13.sp,
                    color = OffWhite
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            repeat(slots) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index < filledSlots) Blurple
                                            else Color(0xFF3A3A3A)
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${offer.currentPlayers}/${offer.targetPlayers} players",
                            fontSize = 12.sp,
                            color = OffWhite
                        )
                    }
                    Button(
                        onClick = if (isJoined) onOfferLeave else onOfferJoin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isJoined) Color(0xFFD20000) else Blurple
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isJoined) "Leave" else "Join",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun OfferCardPreview() {
    val offer = Offer(1, "Looking for ranked teammates", 2, 5, Videogame(1, "Valorant"), 1, "alex123")
    OfferCard(offer, onOfferClick = {}, onOfferJoin = {})
}
