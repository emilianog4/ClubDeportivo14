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
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        // Dentro de onCreate()

        etNombre = findViewById(R.id.et_nombre)
        etApellido = findViewById(R.id.et_apellido) // <-- Añadir
        etDni = findViewById(R.id.et_dni)
        etFechaNac = findViewById(R.id.et_fecha_nac) // <-- Añadir
        etCelular = findViewById(R.id.et_celular) // <-- Añadir
        etDomicilio = findViewById(R.id.et_domicilio) // <-- Añadir
        etEmail = findViewById(R.id.et_email) // <-- Añadir
        chkAptoFisico = findViewById(R.id.cb_apto_fisico)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnCancelar = findViewById(R.id.btn_cancelar)



        btnCancelar.setOnClickListener {
            mostrarDialogoCancelar()
        }


        btnGuardar.setOnClickListener {
            // 1. Instanciamos nuestro AdminSQLiteOpenHelper
            // El 'this' es el contexto de la Activity.
            // "clubDeportivo14.db" será el nombre del archivo de la base de datos.
            val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 1)

            // 2. Recolectamos los datos de la interfaz
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val dni = etDni.text.toString()
            val fechaNac = etFechaNac.text.toString()
            val celular = etCelular.text.toString()
            val domicilio = etDomicilio.text.toString()
            val email = etEmail.text.toString()
            val aptoFisico = chkAptoFisico.isChecked

            // 3. Validamos que los campos no estén vacíos
            if (nombre.isNotEmpty() && apellido.isNotEmpty() && dni.isNotEmpty() && fechaNac.isNotEmpty() &&
                celular.isNotEmpty() && domicilio.isNotEmpty() && email.isNotEmpty()) {

                // 4. Creamos el objeto Socio con los datos recolectados.
                // El 'id' se pone como null porque la base de datos lo generará automáticamente.
                // La 'fechaInscripcion' se pone vacía porque la base de datos la pondrá por defecto.
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
                    fechaInscripcion = "" // Se deja vacío, la BD usa CURRENT_TIMESTAMP
                )

                // 5. Llamamos a la función para agregar el socio a la base de datos.
                val idSocio = admin.agregarSocio(nuevoSocio)

                // 6. Verificamos si se guardó correctamente y mostramos el diálogo
                if (idSocio > -1) {
                    // Éxito: el socio fue insertado. El 'idSocio' es el ID que le dio la BD.
                    mostrarDialogoExito("Socio registrado correctamente.\nID Socio: $idSocio")
                } else {
                    // Error: algo falló al insertar en la base de datos.
                    Toast.makeText(this, "Error al registrar el socio. DNI duplicado?", Toast.LENGTH_LONG).show()
                }

            } else {
                // Si algún campo está vacío, mostramos un mensaje de error.
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
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

    private fun mostrarDialogoCancelar() {
        AlertDialog.Builder(this)
            .setTitle("Advertencia")
            .setMessage("¿Desea cancelar el alta de usuario?")
            .setPositiveButton("Sí") { _, _ ->
                irMenuSocios()
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun mostrarDialogoExito(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Éxito")
            .setMessage(mensaje) // <-- ¡CORREGIDO! Ahora usa el mensaje dinámico
            .setPositiveButton("Aceptar") { _, _ ->
                preguntarPagoCuota()
            }
            .setCancelable(false) // Buena práctica: evita que se cierre por accidente
            .show()
    }


    private fun preguntarPagoCuota() {
        AlertDialog.Builder(this)
            .setTitle("Pago de cuota")
            .setMessage("¿Desea realizar el pago ahora?")
            .setPositiveButton("Sí") { _, _ ->
                irAPagoSocio()
            }
            .setNegativeButton("No") { _, _ ->
                irMenuSocios()
            }
            .show()
    }

    private fun irMenuSocios() {
        val intent = Intent(this, SocioActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun irAPagoSocio() {
        val intent = Intent(this, FormularioPagoSocioActivity::class.java)
        startActivity(intent)
    }

    // Menú inferior desplegable
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
