package com.example.tiendazapatos

data class Zapato(
    val id: Int = 0,          // Campo opcional para el id (con valor predeterminado de 0)
    val nombre: String,
    val talla: String,
    val precio: Double,
    val cantidad: Int,
    val latitud: Double,
    val longitud: Double
)
