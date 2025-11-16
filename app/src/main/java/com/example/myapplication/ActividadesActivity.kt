package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.database.AdminSQLiteOpenHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.widget.AppCompatButton
import android.widget.TextView
class ActividadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.actividades)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Traer actividades de la BD
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)
        val actividades = admin.obtenerActividades()

        // Armamos el texto completo dentro de la card
        val tvCard = findViewById<TextView>(R.id.tv_detalle_actividades)

        if (actividades.isEmpty()) {
            tvCard.text = "No hay actividades cargadas."
        } else {
            val texto = actividades.joinToString("\n\n") { act ->
                "• ${act.nombre}\n$ ${act.precio}"
            }
            tvCard.text = texto
        }

        // Botón INSCRIPCIÓN → abre pantalla donde sí se muestran las cards
        val btnInscripcion = findViewById<AppCompatButton>(R.id.btn_inscripcion)
        btnInscripcion.setOnClickListener {
            val intent = Intent(this, ActividadesPagoActivity::class.java)
            startActivity(intent)
        }

        // Botón back
        findViewById<ImageButton>(R.id.btn_back_actividades).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Menú inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
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

    // -----------------------------------------
    //  MENU INFERIOR
    // -----------------------------------------

    private fun showUserMenu() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
        bottomSheet.setContentView(view)

        view.findViewById<LinearLayout>(R.id.ll_perfil).setOnClickListener {
            bottomSheet.dismiss()
            startActivity(Intent(this, PerfilUsuarioActivity::class.java))
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
            showSalirDialog()
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
