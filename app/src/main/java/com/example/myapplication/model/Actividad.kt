package com.example.myapplication.model

data class Actividad(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val profesor: String? = "-",
    val horario: String? = "-",
    val cupos: Int = 0
)