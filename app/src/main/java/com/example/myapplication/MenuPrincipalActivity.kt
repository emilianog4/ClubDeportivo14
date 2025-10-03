package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        // Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_actividades = findViewById<Button>(R.id.btn_actividades)
        btn_actividades.setOnClickListener {
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
        }

        val btn_socios = findViewById<Button>(R.id.btn_socios)
        btn_socios.setOnClickListener {
            val intent = Intent(this, SocioActivity::class.java)
            startActivity(intent)
        }

        val btn_pagos = findViewById<Button>(R.id.btn_pagos)
        btn_pagos.setOnClickListener {
            val intent = Intent(this, PagosActivity::class.java)
            startActivity(intent)
        }

        val btn_carnet = findViewById<Button>(R.id.btn_carnet)
        btn_carnet.setOnClickListener {
            val intent = Intent(this, CarnetDigitalActivity::class.java)
            startActivity(intent)
        }

        val btn_rutinas = findViewById<Button>(R.id.btn_rutinas)
        btn_rutinas.setOnClickListener {
            val intent = Intent(this, RutinasActivity::class.java)
            startActivity(intent)
        }

        val btn_nutricion = findViewById<Button>(R.id.btn_nutricion)
        btn_nutricion.setOnClickListener {
            val intent = Intent(this, FichaMedicaNutricionActivity::class.java)
            startActivity(intent)
        }

        val btn_profesores = findViewById<Button>(R.id.btn_profesores)
        btn_profesores.setOnClickListener {
            val intent = Intent(this, ProfesoresActivity::class.java)
            startActivity(intent)
        }

        // BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_qr -> {
                    Toast.makeText(this, "QR", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_menu -> {
                    showUserMenu()
                    true
                }
                else -> false
            }
        }
    }

    private fun showUserMenu() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
        bottomSheet.setContentView(view)

        view.findViewById<LinearLayout>(R.id.ll_perfil).setOnClickListener {
            bottomSheet.dismiss()
            val intent = Intent(this, PerfilUsuarioActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.ll_ajuste).setOnClickListener {
            Toast.makeText(this, "Abrir Ajuste de seguridad", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.ll_politica).setOnClickListener {
            Toast.makeText(this, "Abrir Política de privacidad", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.ll_salir).setOnClickListener {
            bottomSheet.dismiss()
            showSalirDialog() // función existente
        }

        bottomSheet.show()
    }

    private fun showSalirDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Salir")
        builder.setMessage("¿Estás seguro que deseas cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
