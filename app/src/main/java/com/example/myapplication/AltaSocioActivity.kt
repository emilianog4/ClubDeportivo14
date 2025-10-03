package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class AltaSocioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
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


        etNombre = findViewById(R.id.et_nombre)
        etDni = findViewById(R.id.et_dni)
        chkAptoFisico = findViewById(R.id.cb_apto_fisico)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnCancelar = findViewById(R.id.btn_cancelar)


        btnCancelar.setOnClickListener {
            mostrarDialogoCancelar()
        }


        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                mostrarDialogoExito()
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


    private fun validarCampos(): Boolean {
        val nombre = etNombre.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val apto = chkAptoFisico.isChecked

        if (nombre.isEmpty() || dni.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!apto) {
            Toast.makeText(this, "Debe marcar Apto físico", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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


    private fun mostrarDialogoExito() {
        AlertDialog.Builder(this)
            .setTitle("Éxito")
            .setMessage("✅ Socio registrado correctamente.\nID Socio: 12341")
            .setPositiveButton("Aceptar") { _, _ ->
                preguntarPagoCuota()
            }
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
