package com.example.myapplication

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.database.AdminSQLiteOpenHelper
import com.example.myapplication.adapter.PagosVencerAdapter

class HistorialPagosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_pagos)

        val listView = findViewById<ListView>(R.id.list_historial)

        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

        // Traemos el ID del socio
        val idSocio = intent.getIntExtra("idSocio", -1)

        if (idSocio != -1) {

            val lista = admin.obtenerPagosProximosAVencer(idSocio)

            if (lista.isEmpty()) {
                Toast.makeText(this, "No hay pagos próximos a vencer", Toast.LENGTH_SHORT).show()
            } else {
                val adapter = PagosVencerAdapter(this, lista)
                listView.adapter = adapter
            }
        } else {
            Toast.makeText(this, "Error: ID de socio no válido", Toast.LENGTH_SHORT).show()
        }
    }
}
