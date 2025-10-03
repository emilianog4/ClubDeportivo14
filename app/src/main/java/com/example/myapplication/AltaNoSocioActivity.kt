package com.example.myapplication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class AltaNoSocioActivity : AppCompatActivity() {

    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alta_no_socio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alta_no_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias UI
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar)
        val cbApto = findViewById<CheckBox>(R.id.cb_apto_fisico)
        val etTipoActividad = findViewById<EditText>(R.id.et_tipo_actividad)
        val etImporteActividad = findViewById<EditText>(R.id.et_importe_actividad)
        val spFormaPago = findViewById<Spinner>(R.id.sp_forma_pago)

        // Recuperar datos enviados desde la pantalla de actividades
        val actividad = intent.getStringExtra("actividad") ?: ""
        val importe = intent.getStringExtra("importe") ?: ""

        etTipoActividad.setText(actividad)
        etImporteActividad.setText(importe)

        //Spinner de formas de pago
        val opcionesPago = listOf("Efectivo", "Tarjeta de crédito", "Tarjeta de débito", "Transferencia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesPago)
        spFormaPago.adapter = adapter

        //Botón Guardar
        btnGuardar.setOnClickListener {
            if (cbApto.isChecked) {
                mostrarLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    ocultarLoading()
                    mostrarDialogoExito()
                }, 2000) // simula 2 segundos de procesamiento
            } else {
                Toast.makeText(this, "Debe marcar Apto Físico", Toast.LENGTH_SHORT).show()
            }
        }

        //Botón Cancelar
        btnCancelar.setOnClickListener {
            finish() // vuelve a la pantalla anterior
        }

        //Navegación inferior
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

    //Loading
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
    private fun mostrarDialogoExito() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alta No Socio")
        builder.setMessage("No Socio - Pago e inscripción realizada")
        builder.setIcon(R.drawable.ic_check)
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.show()
    }

    // 📋 Menú inferior
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
