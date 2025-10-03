package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

data class Profesor(
    val nombre: String,
    val id: String,
    val especialidad: String,
    val estado: String
)

class ProfesoresListasActivity : AppCompatActivity() {

    private val profesores = listOf(
        Profesor("Juan Jose Perez", "1", "Funcional", "Activo"),
        Profesor("Juan Jose Perez", "2", "Natación", "Activo"),
        Profesor("Juan Jose Perez", "1.2", "Spinning", "Suplente")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesores_listas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listado_profesores)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack = findViewById<ImageButton>(R.id.btn_back_profesores_listas)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val contenedor = findViewById<LinearLayout>(R.id.contenedor_profesores)
        cargarProfesores(contenedor)

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish()
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

    private fun cargarProfesores(contenedor: LinearLayout) {
        contenedor.removeAllViews()
        for (prof in profesores) {
            val tarjeta = layoutInflater.inflate(R.layout.item_profesor, contenedor, false)

            val img = tarjeta.findViewById<ImageView>(R.id.img_profesor)
            val nombre = tarjeta.findViewById<TextView>(R.id.tv_nombre_profesor)
            val especialidad = tarjeta.findViewById<TextView>(R.id.tv_especialidad_profesor)

            nombre.text = "${prof.nombre} - ID_Prof: ${prof.id}"
            especialidad.text = "Especialidad: ${prof.especialidad} (${prof.estado})"

            tarjeta.setOnClickListener {
                mostrarDialogoSeleccion(prof)
            }

            contenedor.addView(tarjeta)
        }
    }

    private fun mostrarDialogoSeleccion(prof: Profesor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Profesor seleccionado")
        builder.setMessage("${prof.nombre}\nEspecialidad: ${prof.especialidad}\nEstado: ${prof.estado}")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showUserMenu() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
        bottomSheet.setContentView(view)

        view.findViewById<LinearLayout>(R.id.ll_perfil).setOnClickListener {
            Toast.makeText(this, "Abrir Perfil", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
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
            Toast.makeText(this, "Cerrar sesión", Toast.LENGTH_SHORT).show()
        }

        bottomSheet.show()
    }
}
