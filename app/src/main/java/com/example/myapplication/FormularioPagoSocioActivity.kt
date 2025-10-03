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

class FormularioPagoSocioActivity : AppCompatActivity() {

    private lateinit var etNroSocio: EditText
    private lateinit var spinnerCuotas: Spinner
    private lateinit var etImporte: EditText
    private lateinit var spinnerPago: Spinner
    private lateinit var etPeriodo: EditText
    private lateinit var btnPagar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Referencias UI
        etNroSocio = findViewById(R.id.et_nro_socio)
        spinnerCuotas = findViewById(R.id.spinner_cuotas)
        etImporte = findViewById(R.id.et_importe)
        spinnerPago = findViewById(R.id.spinner_pago)
        etPeriodo = findViewById(R.id.et_periodo)
        btnPagar = findViewById(R.id.btn_pagar)
        btnCancelar = findViewById(R.id.btn_cancelar_pago)

        //Spinner de cuotas (1 a 12)
        val cuotas = (1..12).map { it.toString() }
        val cuotasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)
        spinnerCuotas.adapter = cuotasAdapter

        //Spinner formas de pago
        val formasPago = listOf("Tarjeta de crédito", "Tarjeta de débito", "Efectivo", "Transferencia")
        val formasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, formasPago)
        spinnerPago.adapter = formasAdapter

        // 🧾 Botón Pagar
        btnPagar.setOnClickListener {
            if (validarCampos()) {
                mostrarDialogoProcesando()
            }
        }

        //Botón Cancelar
        btnCancelar.setOnClickListener {
            mostrarDialogoCancelar()
        }

        // 🔸 BottomNavigationView
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

    //Validar datos obligatorios
    private fun validarCampos(): Boolean {
        if (etNroSocio.text.isNullOrBlank()) {
            Toast.makeText(this, "Ingrese un número de socio válido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etImporte.text.isNullOrBlank()) {
            Toast.makeText(this, "Ingrese el importe", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etPeriodo.text.isNullOrBlank()) {
            Toast.makeText(this, "Ingrese el período", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Loading de procesamiento
    private fun mostrarDialogoProcesando() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        dialog.show()

        // ⏳ Simulamos proceso de pago 2 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            mostrarDialogoExito()
        }, 2000)
    }

    //Diálogo de éxito
    private fun mostrarDialogoExito() {
        AlertDialog.Builder(this)
            .setTitle("Pago realizado")
            .setMessage("✅ Pago registrado correctamente.")
            .setPositiveButton("Aceptar") { _, _ ->
                volverAlMenuPrincipal()
            }
            .show()
    }

    //Diálogo de cancelación
    private fun mostrarDialogoCancelar() {
        AlertDialog.Builder(this)
            .setTitle("Advertencia")
            .setMessage("¿Desea cancelar el pago?")
            .setPositiveButton("Sí") { _, _ ->
                volverAlMenuPagos()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun volverAlMenuPagos() {
        val intent = Intent(this, PagosActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun volverAlMenuPrincipal() {
        val intent = Intent(this, MenuPrincipalActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    //Menú inferior
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
            showSalirDialog()
        }

        bottomSheet.show()
    }

    private fun showSalirDialog() {
        val builder = AlertDialog.Builder(this)
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
