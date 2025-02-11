package com.example.zapatostiendaapp.database.model

data class Shoe(
    val id: Long = 0,
    val name: String,
    val price: Double,
    val size: Double,
    val location: String
)