package com.example.reacht_android.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reacht_android.model.Game
import com.example.reacht_android.model.Offer
import com.example.reacht_android.ui.theme.LightGrey

@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGrey,
            contentColor = Color.White
        ),
    ) {
        Text(
            text = offer.game.name,
            modifier = Modifier.padding(top = 30.dp, start = 18.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Players: ${offer.currentPlayersNumber}/${offer.targetPlayersNumber}",
            modifier = Modifier.padding(top = 20.dp, start = 18.dp),
            fontSize = 18.sp,
        )
    }
}


@Preview
@Composable
fun OfferCardPreview() {
    val offer = Offer(Game("Call Of Duty"), 0, 5)
    OfferCard(offer)
}