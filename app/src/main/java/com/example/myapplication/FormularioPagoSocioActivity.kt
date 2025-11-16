package com.example.myapplication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.database.AdminSQLiteOpenHelper
import com.example.myapplication.model.Socio
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class FormularioPagoSocioActivity : AppCompatActivity() {

    private lateinit var etNroSocio: EditText
    private lateinit var spinnerCuotas: Spinner
    private lateinit var etImporte: EditText
    private lateinit var spinnerPago: Spinner
    private lateinit var etPeriodo: EditText
    private lateinit var etBuscar: EditText
    private lateinit var btnPagar: Button
    private lateinit var btnCancelar: Button

    private var socioActual: Socio? = null

    private val formatoUser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val precioBaseMensual = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias UI
        etBuscar = findViewById(R.id.et_buscar_socio)
        etNroSocio = findViewById(R.id.et_nro_socio)
        spinnerCuotas = findViewById(R.id.spinner_cuotas)
        etImporte = findViewById(R.id.et_importe)
        spinnerPago = findViewById(R.id.spinner_pago)
        etPeriodo = findViewById(R.id.et_periodo)
        btnPagar = findViewById(R.id.btn_pagar)
        btnCancelar = findViewById(R.id.btn_cancelar_pago)
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

        // =====================================================
        // 1) Si vengo desde AltaSocioActivity
        // =====================================================
        val idSocioIntent = intent.getLongExtra("nroSocio", -1)
        if (idSocioIntent != -1L) {
            cargarSocio(admin, idSocioIntent.toInt())
            val cuotasActuales = 1
            actualizarImporte(cuotasActuales)
            socioActual?.let {
                calcularPeriodo(it.fechaInscripcion, cuotasActuales, desdeAltaSocio = true)
            }
        }

        // =====================================================
        // 2) Spinner de cuotas
        // =====================================================
        val cuotas = (1..12).map { it.toString() }
        spinnerCuotas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)

        spinnerCuotas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                val cantCuotas = cuotas[pos].toInt()
                actualizarImporte(cantCuotas)

                // Si el usuario cargó el ID manualmente, recalcula período desde HOY
                val idSocioManual = etNroSocio.text.toString().toIntOrNull()
                // SOLO recalculamos si ya hay socioActual cargado
                if (socioActual != null) {
                    calcularPeriodo(socioActual!!.fechaInscripcion, cantCuotas, false)
                    actualizarImporte(cantCuotas)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // =====================================================
        // 3) Formas de pago
        // =====================================================
        val formasPago = listOf("Tarjeta de crédito", "Tarjeta de débito", "Efectivo", "Transferencia")
        spinnerPago.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, formasPago)

        // =====================================================
        // 4) Buscar socio por lupa
        // =====================================================
        val btnBuscar = findViewById<ImageButton>(R.id.btn_buscar)
        btnBuscar.setOnClickListener {
            buscarSocioDesdeUI(admin)
        }

        // =====================================================
        // 5) ENTER para buscar socio automáticamente
        // =====================================================
        etNroSocio.setOnEditorActionListener { _, _, _ ->
            buscarSocioDesdeUI(admin)
            true
        }

        etBuscar.setOnEditorActionListener { _, _, _ ->
            buscarSocioDesdeUI(admin)
            true
        }



        // Botón pagar
        btnPagar.setOnClickListener {
            if (validarCampos()) mostrarDialogoProcesando()
        }

        btnCancelar.setOnClickListener { mostrarDialogoCancelar() }

        val btnBack = findViewById<ImageButton>(R.id.btn_back_pago)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Bottom nav
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MenuPrincipalActivity::class.java)); true }
                R.id.nav_qr -> { Toast.makeText(this, "QR", Toast.LENGTH_SHORT).show(); true }
                R.id.nav_menu -> { showUserMenu(); true }
                else -> false
            }
        }
    }

    // ==========================================================
    // FUNCIONES CLAVE (BUSCADOR + PERÍODO + IMPORTE)
    // ==========================================================

    private fun buscarSocioDesdeUI(admin: AdminSQLiteOpenHelper) {

        // 1) Tomamos valor del campo superior
        val textoBusqueda = etBuscar.text.toString().trim()

        // 2) Tomamos valor del Nro Socio real
        val nroSocioTexto = etNroSocio.text.toString().trim()

        // 3) Elegimos cuál usar
        val id = when {
            nroSocioTexto.isNotEmpty() -> nroSocioTexto.toIntOrNull()
            textoBusqueda.isNotEmpty() -> textoBusqueda.toIntOrNull()
            else -> null
        }

        if (id == null) {
            Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        val socio = admin.buscarSocioPorId(id)

        if (socio == null) {
            Toast.makeText(this, "No existe socio con ese ID", Toast.LENGTH_SHORT).show()
            limpiarCamposBuscador()
            return
        }

        socioActual = socio

        // Mostramos datos
        etBuscar.setText("${socio.nombre} ${socio.apellido}")
        etNroSocio.setText(socio.id.toString())

        // Recalcular
        val cuotasActuales = spinnerCuotas.selectedItem.toString().toInt()
        actualizarImporte(cuotasActuales)
        calcularPeriodo(socio.fechaInscripcion, cuotasActuales, false)
    }

    private fun cargarSocio(admin: AdminSQLiteOpenHelper, id: Int) {
        val socio = admin.buscarSocioPorId(id)
        if (socio == null) {
            Toast.makeText(this, "No existe socio con ese ID", Toast.LENGTH_SHORT).show()
            limpiarCamposBuscador()
            return
        }

        socioActual = socio
        etBuscar.setText("${socio.nombre} ${socio.apellido}")
        etNroSocio.setText(socio.id.toString())
    }


    private fun limpiarSocio() {
        socioActual = null
        etBuscar.setText("")
        etPeriodo.setText("")
    }

    private fun actualizarImporte(cuotas: Int) {
        val total = precioBaseMensual * cuotas
        etImporte.setText(total.toString())
    }

    private fun calcularPeriodo(fechaAlta: String?, cuotas: Int, desdeAltaSocio: Boolean) {

        val cal = Calendar.getInstance()

        if (desdeAltaSocio && !fechaAlta.isNullOrBlank()) {
            try {
                val formatoBd =
                    if (fechaAlta.contains(":"))
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    else
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val fecha = formatoBd.parse(fechaAlta)
                if (fecha != null) cal.time = fecha

            } catch (_: Exception) {}
        }

        // SI vengo del módulo PAGO → SIEMPRE fecha actual
        if (!desdeAltaSocio) {
            cal.time = Date()
        }

        val inicio = formatoUser.format(cal.time)

        cal.add(Calendar.MONTH, cuotas)
        val fin = formatoUser.format(cal.time)

        etPeriodo.setText("$inicio ➜ $fin")
    }

    // ==========================================================
    // VALIDACIONES / DIÁLOGOS / MENÚ
    // ==========================================================

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

    private fun mostrarDialogoProcesando() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            registrarPagoEnBD()
            mostrarDialogoExito()
        }, 2000)
    }


    private fun mostrarDialogoExito() {
        AlertDialog.Builder(this)
            .setTitle("Pago realizado")
            .setMessage("✅ Pago registrado correctamente.")
            .setPositiveButton("Aceptar") { _, _ ->
                startActivity(Intent(this, MenuPrincipalActivity::class.java))
            }
            .show()
    }

    private fun mostrarDialogoCancelar() {
        AlertDialog.Builder(this)
            .setTitle("Advertencia")
            .setMessage("¿Desea cancelar el pago?")
            .setPositiveButton("Sí") { _, _ ->
                startActivity(Intent(this, PagosActivity::class.java))
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun registrarPagoEnBD() {
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

        val idSocio = etNroSocio.text.toString().toInt()
        val cuotas = spinnerCuotas.selectedItem.toString().toInt()
        val importe = etImporte.text.toString().toDouble()
        val formaPago = spinnerPago.selectedItem.toString()
        val periodo = etPeriodo.text.toString()

        val idPago = admin.registrarPagoCuotaSocio(
            idSocio,
            cuotas,
            importe,
            formaPago,
            periodo
        )
    }
    private fun limpiarCamposBuscador() {
        socioActual = null
        etBuscar.setText("")
        etPeriodo.setText("")
    }
    private fun showUserMenu() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
        bottomSheet.setContentView(view)

        view.findViewById<LinearLayout>(R.id.ll_perfil).setOnClickListener {
            bottomSheet.dismiss()
            startActivity(Intent(this, PerfilUsuarioActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.ll_ajuste).setOnClickListener {
            Toast.makeText(this, "Abrir Ajustes", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.ll_salir).setOnClickListener {
            bottomSheet.dismiss()
            AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Seguro quiere cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        bottomSheet.show()
    }
}
