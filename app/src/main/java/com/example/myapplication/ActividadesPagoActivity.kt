package com.example.myapplication

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.helpers.ValidacionSocioHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.Intent
import android.widget.ImageButton

class ActividadesPagoActivity : AppCompatActivity() {

    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_por_actividad)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_back_pago_actividad)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val tarjetaFutbol = findViewById<LinearLayout>(R.id.card_futbol)
        tarjetaFutbol.setOnClickListener {
            ValidacionSocioHelper.mostrarDialogoSeleccionSocio(
                this,
                onSocioSeleccionado = {
                    ValidacionSocioHelper.mostrarDialogoValidacionSocio(this) { nroSocio ->
                        mostrarLoading() // ⏳ mostramos el loading
                        Handler(Looper.getMainLooper()).postDelayed({
                            ocultarLoading()
                            mostrarDialogoExito("Socio activo, inscripción correcta")
                        }, 2000) // ⏰ simulamos 2 segundos de validación
                    }
                },
                onNoSocioSeleccionado = {
                    val intent = Intent(this, AltaNoSocioActivity::class.java)
                    intent.putExtra("actividad", "Fútbol")
                    intent.putExtra("importe", "$5.000,00")
                    startActivity(intent)
                }

            )
        }

        // Navegación inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
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

    // Loading reutilizable
    private fun mostrarLoading() {
        loadingDialog = Dialog(this)
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun ocultarLoading() {
        loadingDialog?.dismiss()
    }

    // Diálogo de éxito
    private fun mostrarDialogoExito(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Inscripción")
        builder.setMessage(mensaje)
        builder.setIcon(R.drawable.ic_check)
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    // Menú inferior
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
