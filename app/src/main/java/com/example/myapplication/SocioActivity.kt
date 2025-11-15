package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.SocioAdapter
import com.example.myapplication.database.AdminSQLiteOpenHelper

class SocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_alta = findViewById<Button>(R.id.btn_alta)
        btn_alta.setOnClickListener {
            val intent = Intent(this, AltaSocioActivity::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    // Opcional: para que no se acumulen activities en la pila
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_qr -> {
                    Toast.makeText(this, "QR", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_menu -> {
                    showUserMenu() // Ahora se usa BottomSheetDialog
                    true
                }
                else -> false
            }
        }
        // 1. Buscamos el RecyclerView en nuestro layout
                val rvSocios = findViewById<RecyclerView>(R.id.rv_socios)

        // 2. Creamos una instancia de nuestro AdminSQLiteOpenHelper
                val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 1)

        // 3. Obtenemos la lista de todos los socios desde la base de datos
                val listaDeSocios = admin.obtenerTodosLosSocios()

        // 4. Creamos una instancia de nuestro adaptador, pasándole la lista de socios
                val adapter = SocioAdapter(listaDeSocios)

        // 5. Configuramos el RecyclerView
                rvSocios.layoutManager = LinearLayoutManager(this) // Le decimos que muestre los items en una lista vertical
                rvSocios.adapter = adapter // Le asignamos nuestro adaptador

        cargarSocios()

    }

    override fun onResume() {
        super.onResume()
        cargarSocios() // Y llama aquí también para que se refresque al volver
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

    private fun cargarSocios() {    val rvSocios = findViewById<RecyclerView>(R.id.rv_socios)
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 1)
        val listaDeSocios = admin.obtenerTodosLosSocios()
        val adapter = SocioAdapter(listaDeSocios)
        rvSocios.layoutManager = LinearLayoutManager(this)
        rvSocios.adapter = adapter
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


