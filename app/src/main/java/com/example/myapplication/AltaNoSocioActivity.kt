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
import com.example.myapplication.database.AdminSQLiteOpenHelper
import com.example.myapplication.model.NoSocio

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

        val btnBack = findViewById<ImageButton>(R.id.btn_back_no_socio)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar)
        val cbApto = findViewById<CheckBox>(R.id.cb_apto_fisico)
        val etTipoActividad = findViewById<EditText>(R.id.et_tipo_actividad)
        val etImporteActividad = findViewById<EditText>(R.id.et_importe_actividad)
        val spFormaPago = findViewById<Spinner>(R.id.sp_forma_pago)

        val actividad = intent.getStringExtra("actividad") ?: ""
        val importe = intent.getStringExtra("importe") ?: ""

        findViewById<EditText>(R.id.et_tipo_actividad).setText(actividad)
        findViewById<EditText>(R.id.et_importe_actividad).setText(importe)

        val opcionesPago = listOf("Efectivo", "Tarjeta de crédito", "Tarjeta de débito", "Transferencia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesPago)
        spFormaPago.adapter = adapter

        btnGuardar.setOnClickListener {

            val etDni = findViewById<EditText>(R.id.et_dni_no_socio)
            val dniIngresado = etDni.text.toString().trim()

            val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

            //VALIDAR DNI VACÍO
            if (dniIngresado.isEmpty()) {
                Toast.makeText(this, "Debe ingresar un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //VALIDAR QUE NO SEA SOCIO
            val socioExiste = admin.buscarSocioPorDni(dniIngresado)

            if (socioExiste != null) {
                Toast.makeText(this, "Ese DNI ya pertenece a un socio registrado", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // --- VALIDACIONES EXISTENTES -----
            val actividadTexto = etTipoActividad.text.toString().trim()
            val importeTexto = etImporteActividad.text.toString().trim()
            val formaPagoSeleccionada = spFormaPago.selectedItem?.toString() ?: ""

            if (actividadTexto.isEmpty()) {
                Toast.makeText(this, "Debe indicar el tipo de actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (importeTexto.isEmpty()) {
                Toast.makeText(this, "Debe indicar el importe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val importeDouble = importeTexto.toDoubleOrNull()
            if (importeDouble == null || importeDouble <= 0.0) {
                Toast.makeText(this, "El importe debe ser un número válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cbApto.isChecked) {
                Toast.makeText(this, "Debe marcar Apto Físico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (formaPagoSeleccionada.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar una forma de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- GUARDAR EN BD ---
            mostrarLoading()

            val noSocio = NoSocio(
                tipoActividad = actividadTexto,
                importe = importeDouble,
                formaPago = formaPagoSeleccionada,
                aptoFisico = cbApto.isChecked
            )

            val idInsertado = admin.agregarNoSocio(noSocio)

            admin.registrarPagoNoSocioActividad(
                idNoSocio = idInsertado.toInt(),
                idActividad = intent.getIntExtra("idActividad", -1),
                importe = intent.getStringExtra("importe")!!.toDouble(),
                formaPago = formaPagoSeleccionada
            )

            Handler(Looper.getMainLooper()).postDelayed({
                ocultarLoading()

                if (idInsertado > 0) {
                    mostrarDialogoExito()
                } else {
                    Toast.makeText(this, "Error al registrar el No Socio", Toast.LENGTH_LONG).show()
                }
            }, 1000)
        }

        //Botón Cancelar
        btnCancelar.setOnClickListener {
            finish()
        }

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

    private fun mostrarLoading() {
        loadingDialog = Dialog(this)
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun ocultarLoading() {
        loadingDialog?.dismiss()
    }

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
            Toast.makeText(this, "Cerrar sesión", Toast.LENGTH_SHORT).show()
        }

        bottomSheet.show()
    }
}
