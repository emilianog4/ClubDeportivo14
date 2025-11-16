package com.example.myapplication.model

data class NoSocio(
    val id: Int? = null,
    val tipoActividad: String,
    val importe: Double,
    val formaPago: String,
    val aptoFisico: Boolean,
    val fechaInscripcion: String? = null
)
