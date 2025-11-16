package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.TextWatcher
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.SocioAdapter
import android.widget.EditText
import android.text.Editable
import com.example.myapplication.database.AdminSQLiteOpenHelper

class SocioActivity : AppCompatActivity() {
    // Declaramos vistas del cuadro de detalle
    private lateinit var llDetalle: LinearLayout
    private lateinit var tvNroSocio: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvInscripcion: TextView
    private lateinit var tvCuota: TextView
    private lateinit var tvActividades: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos las vistas del detalle
        llDetalle = findViewById(R.id.ll_detalle)
        tvNroSocio = findViewById(R.id.tv_nro_socio)
        tvNombre = findViewById(R.id.tv_nombre)
        tvInscripcion = findViewById(R.id.tv_inscripcion)
        tvCuota = findViewById(R.id.tv_cuota)
        tvActividades = findViewById(R.id.tv_actividades)

        val etBuscar = findViewById<EditText>(R.id.et_buscar)
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val socio = admin.buscarSocioPorNombre(query)
                    if (socio != null) {
                        // Socio encontrado, mostramos los datos
                        llDetalle.visibility = View.VISIBLE
                        tvNroSocio.text = "Nro. Socio: ${socio.id}"
                        tvNombre.text = "Nombre y Apellido: ${socio.nombre} ${socio.apellido}"
                        tvInscripcion.text = "Inscripción: ${socio.fechaInscripcion}"
                        // Datos de ejemplo para cuota y actividades (debes adaptarlos a tu lógica)
                        tvCuota.text = "Cuota: Al día"
                        tvActividades.text = "Actividades: Fútbol, Tenis"
                    } else {
                        // No se encontró socio, ocultamos el cuadro
                        llDetalle.visibility = View.GONE
                    }
                } else {
                    // Si el buscador está vacío, ocultamos el cuadro
                    llDetalle.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val btn_alta = findViewById<Button>(R.id.btn_alta)
        btn_alta.setOnClickListener {
            val intent = Intent(this, AltaSocioActivity::class.java)
            startActivity(intent)
        }

        val btnRecordatorio = findViewById<AppCompatButton>(R.id.btn_recordatorio)
        btnRecordatorio.setOnClickListener {
            // Mostrar el mensaje en pantalla.
            Toast.makeText(this, "Recordatorio de pago enviado", Toast.LENGTH_SHORT).show()
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    // Opcional: para que no se acumulen activities en la pila
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_qr -> {
                    Toast.makeText(this, "QR", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_menu -> {
                    showUserMenu() // Ahora se usa BottomSheetDialog
                    true
                }
                else -> false
            }
        }
        // 1. Buscamos el RecyclerView en nuestro layout
                val rvSocios = findViewById<RecyclerView>(R.id.rv_socios)

        // 2. Obtenemos la lista de todos los socios desde la base de datos
                val listaDeSocios = admin.obtenerTodosLosSocios()

        // 3. Creamos una instancia de nuestro adaptador, pasándole la lista de socios
                val adapter = SocioAdapter(listaDeSocios)

        // 4. Configuramos el RecyclerView
                rvSocios.layoutManager = LinearLayoutManager(this) // Le decimos que muestre los items en una lista vertical
                rvSocios.adapter = adapter // Le asignamos nuestro adaptador

        cargarSocios()

    }

    override fun onResume() {
        super.onResume()
        cargarSocios() // LLamamos aca para que se refresque al volver
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
            showSalirDialog() // función existente
        }

        bottomSheet.show()
    }

    private fun cargarSocios() {    val rvSocios = findViewById<RecyclerView>(R.id.rv_socios)
        val admin = AdminSQLiteOpenHelper(this, "clubDeportivo14.db", null, 6)
        val listaDeSocios = admin.obtenerTodosLosSocios()
        val adapter = SocioAdapter(listaDeSocios)
        rvSocios.layoutManager = LinearLayoutManager(this)
        rvSocios.adapter = adapter
    }

    private fun showSalirDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
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


