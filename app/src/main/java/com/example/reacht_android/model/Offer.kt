package com.example.reacht_android.model

data class Offer(
    val offerId: Int,
    val description: String,
    val currentPlayers: Int,
    val targetPlayers: Int,
    val videogame: Videogame,
    val creatorId: Int,
    val creatorUsername: String
)
