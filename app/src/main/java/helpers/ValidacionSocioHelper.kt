package com.example.myapplication.helpers

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

object ValidacionSocioHelper {

    fun mostrarDialogoSeleccionSocio(
        activity: AppCompatActivity,
        onSocioSeleccionado: () -> Unit,
        onNoSocioSeleccionado: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Seleccionar tipo de usuario")
            .setMessage("¿Es socio o no socio?")
            .setPositiveButton("Socio") { dialog, _ ->
                dialog.dismiss()
                onSocioSeleccionado()
            }
            .setNegativeButton("No socio") { dialog, _ ->
                dialog.dismiss()
                onNoSocioSeleccionado()
            }
            .show()
    }

    fun mostrarDialogoValidacionSocio(
        activity: AppCompatActivity,
        onValidado: (String) -> Unit
    ) {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_validar_socio, null)
        val etNroSocio = view.findViewById<EditText>(R.id.et_nro_socio)

        AlertDialog.Builder(activity)
            .setTitle("Validar socio")
            .setView(view)
            .setPositiveButton("Validar") { dialog, _ ->
                val nro = etNroSocio.text.toString().trim()
                if (nro.isNotEmpty()) {
                    onValidado(nro)
                } else {
                    Toast.makeText(activity, "Ingrese un número de socio válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
