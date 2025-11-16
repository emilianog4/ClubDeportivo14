package com.example.myapplication

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.adapter.PagosVencerAdapter
import com.example.myapplication.database.AdminSQLiteOpenHelper

class PagosVencerGlobalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_pagos)

        val listView = findViewById<ListView>(R.id.list_historial)
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

        val lista = admin.obtenerPagosProximosAVencerGlobal()
        val adapter = PagosVencerAdapter(this, lista)
        listView.adapter = adapter
    }
}
