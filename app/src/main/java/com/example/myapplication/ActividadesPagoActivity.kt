package com.example.myapplication

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.Toast
import android.content.Intent
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.database.AdminSQLiteOpenHelper
import com.example.myapplication.helpers.ValidacionSocioHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class ActividadesPagoActivity : AppCompatActivity() {

    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_por_actividad)) { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(inset.left, inset.top, inset.right, inset.bottom)
            insets
        }

        // ----------- CARGAR ACTIVIDADES -----------
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)
        val actividades = admin.obtenerActividades()

        val contenedor = findViewById<LinearLayout>(R.id.contenedor_cards)

        if (actividades.isEmpty()) {
            Toast.makeText(this, "No hay actividades cargadas", Toast.LENGTH_SHORT).show()
            return
        }

        val inflater = LayoutInflater.from(this)

        actividades.forEach { actividad ->

            val card = inflater.inflate(R.layout.item_actividad_card, contenedor, false)

            val tvNombre = card.findViewById<TextView>(R.id.tv_nombre)
            val tvProfesor = card.findViewById<TextView>(R.id.tv_profesor)
            val tvHorario = card.findViewById<TextView>(R.id.tv_horario)
            val tvCupos = card.findViewById<TextView>(R.id.tv_cupos)
            val btnInscribir = card.findViewById<AppCompatButton>(R.id.btn_inscribir)

            tvNombre.text = actividad.nombre
            tvProfesor.text = "Profesor/a: ${actividad.profesor ?: "-"}"
            tvHorario.text = "Horario: ${actividad.horario ?: "-"}"
            tvCupos.text = "Cupos: ${actividad.cupos}"

            btnInscribir.setOnClickListener {

                if (actividad.cupos <= 0) {
                    Toast.makeText(this, "No hay cupos disponibles", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mostrarPopupInscripcion(actividad.id, actividad.nombre, actividad.precio)
            }


            contenedor.addView(card)
        }

        // ----------- BOTÓN BACK -----------
        val btnBack = findViewById<ImageButton>(R.id.btn_back_pago_actividad)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ----------- BOTTOM NAV -----------
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { finish(); true }
                R.id.nav_qr -> { Toast.makeText(this, "QR", Toast.LENGTH_SHORT).show(); true }
                R.id.nav_menu -> { showUserMenu(); true }
                else -> false
            }
        }
    }

    // ---------------------- POPUP SELECCIÓN SOCIO ----------------------
    private fun mostrarPopupInscripcion(idActividad: Int, nombre: String, precio: Double) {
        ValidacionSocioHelper.mostrarDialogoSeleccionSocio(
            this,
            onSocioSeleccionado = {
                ValidacionSocioHelper.mostrarDialogoValidacionSocio(this) { nroSocioString ->
                    val nroSocio = nroSocioString.toIntOrNull()
                    if (nroSocio == null) {
                        Toast.makeText(this, "Número incorrecto", Toast.LENGTH_SHORT).show()
                        return@mostrarDialogoValidacionSocio
                    }
                    // VALIDAR SI EL SOCIO EXISTE EN BD
                    val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)
                    if (!admin.existeSocioPorId(nroSocio)) {
                        Toast.makeText(this, "No existe un socio con ese número", Toast.LENGTH_LONG).show()
                        return@mostrarDialogoValidacionSocio
                    }

                    procesarPagoSocio(nroSocio, idActividad, precio, nombre)

                }
            },
            onNoSocioSeleccionado = {
                val intent = Intent(this, AltaNoSocioActivity::class.java)
                intent.putExtra("actividad", nombre)
                intent.putExtra("idActividad", idActividad)
                intent.putExtra("importe", precio.toString())
                startActivity(intent)
            }
        )
    }

    // ---------------------- PROCESAR PAGO SOCIO ----------------------
    private fun procesarPagoSocio(nroSocio: Int, idActividad: Int, precio: Double, nombre: String) {
        mostrarLoading()

        Handler(Looper.getMainLooper()).postDelayed({

            val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

            admin.registrarPagoSocioActividad(
                idSocio = nroSocio,
                idActividad = idActividad,
                importe = precio,
                formaPago = "Cuota Actividad"
            )

            ocultarLoading()
            mostrarDialogoExito("Inscripción correcta a $nombre.")

        }, 1500)
    }

    // ---------------------- UI HELPERS ----------------------
    private fun mostrarLoading() {
        loadingDialog = Dialog(this)
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun ocultarLoading() {
        loadingDialog?.dismiss()
    }

    private fun mostrarDialogoExito(msg: String) {
        AlertDialog.Builder(this)
            .setTitle("Inscripción")
            .setMessage(msg)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun showUserMenu() {
        val bottom = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
        bottom.setContentView(view)

        view.findViewById<LinearLayout>(R.id.ll_perfil).setOnClickListener {
            bottom.dismiss()
            startActivity(Intent(this, PerfilUsuarioActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.ll_salir).setOnClickListener {
            bottom.dismiss()
            Toast.makeText(this, "Cerrar sesión", Toast.LENGTH_SHORT).show()
        }

        bottom.show()
    }
}
