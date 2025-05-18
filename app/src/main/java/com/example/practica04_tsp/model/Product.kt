package com.example.practica04_tsp.model

import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "", // Valor por defecto
    val budget: Double = 0.0, // Valor por defecto
    val priority: Int = 0, // Valor por defecto
    val imageUrl: String = ""
)