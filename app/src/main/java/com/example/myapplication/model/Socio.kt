package com.example.myapplication.model

// dataClass Socio
data class Socio(
    val id: Int? = null,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val fechaNacimiento: String, // Usamos String, como en la BD
    val celular: String,
    val domicilio: String,
    val email: String,
    val aptoFisico: Boolean, // Usamos Boolean, es más intuitivo en el código Kotlin
    val fechaInscripcion: String
)
