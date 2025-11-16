package com.example.myapplication

import android.content.Intent
import android.os.Bundle
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

class AltaSocioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etFechaNac: EditText
    private lateinit var etCelular: EditText
    private lateinit var etDomicilio: EditText
    private lateinit var etEmail: EditText
    private lateinit var chkAptoFisico: CheckBox
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var ultimoIdCreado: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alta_socio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alta_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_back_alta)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ---- Inicialización de campos ----
        etNombre = findViewById(R.id.et_nombre)
        etApellido = findViewById(R.id.et_apellido)
        etDni = findViewById(R.id.et_dni)
        etFechaNac = findViewById(R.id.et_fecha_nac)
        etCelular = findViewById(R.id.et_celular)
        etDomicilio = findViewById(R.id.et_domicilio)
        etEmail = findViewById(R.id.et_email)
        chkAptoFisico = findViewById(R.id.cb_apto_fisico)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnCancelar = findViewById(R.id.btn_cancelar)

        btnCancelar.setOnClickListener { mostrarDialogoCancelar() }

        btnGuardar.setOnClickListener { guardarSocio() }

        configurarBottomNav()
    }

    // -------------------- GUARDAR SOCIO --------------------
    private fun guardarSocio() {
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

        val nombre = etNombre.text.toString()
        val apellido = etApellido.text.toString()
        val dni = etDni.text.toString()
        val fechaNac = etFechaNac.text.toString()
        val celular = etCelular.text.toString()
        val domicilio = etDomicilio.text.toString()
        val email = etEmail.text.toString()
        val aptoFisico = chkAptoFisico.isChecked

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() ||
            fechaNac.isEmpty() || celular.isEmpty() ||
            domicilio.isEmpty() || email.isEmpty()) {

            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear socio correctamente
        val nuevoSocio = Socio(
            id = null,
            nombre = nombre,
            apellido = apellido,
            dni = dni,
            fechaNacimiento = fechaNac,
            celular = celular,
            domicilio = domicilio,
            email = email,
            aptoFisico = aptoFisico,
            fechaInscripcion = ""
        )

        val idSocio = admin.agregarSocio(nuevoSocio)

        ultimoIdCreado = idSocio

        if (idSocio > -1) {
            mostrarDialogoExito("Socio registrado correctamente.\nID Socio: $idSocio")
        } else {
            Toast.makeText(this, "Error al registrar el socio. DNI duplicado?", Toast.LENGTH_LONG).show()
        }
    }

    // -------------------- DIÁLOGOS --------------------
    private fun mostrarDialogoCancelar() {
        AlertDialog.Builder(this)
            .setTitle("Advertencia")
            .setMessage("¿Desea cancelar el alta de usuario?")
            .setPositiveButton("Sí") { _, _ -> irMenuSocios() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarDialogoExito(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Éxito")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { _, _ -> preguntarPagoCuota() }
            .setCancelable(false)
            .show()
    }

    private fun preguntarPagoCuota() {
        AlertDialog.Builder(this)
            .setTitle("Pago de cuota")
            .setMessage("¿Desea realizar el pago ahora?")
            .setPositiveButton("Sí") { _, _ ->
                val intent = Intent(this, FormularioPagoSocioActivity::class.java)
                intent.putExtra("nroSocio", ultimoIdCreado)
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ -> irMenuSocios() }
            .show()
    }

    // -------------------- NAVEGACIÓN --------------------
    private fun irMenuSocios() {
        val intent = Intent(this, SocioActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun configurarBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MenuPrincipalActivity::class.java))
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
            startActivity(Intent(this, PerfilUsuarioActivity::class.java))
        }
        view.findViewById<LinearLayout>(R.id.ll_salir).setOnClickListener {
            bottomSheet.dismiss()
            showSalirDialog()
        }

        bottomSheet.show()
    }

    private fun showSalirDialog() {
        AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
