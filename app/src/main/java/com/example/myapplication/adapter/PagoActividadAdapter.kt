package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Actividad

class PagoActividadAdapter(
    private val lista: List<Actividad>,
    private val onInscribirClick: (Actividad) -> Unit
) : RecyclerView.Adapter<PagoActividadAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tv_nombre)
        val precio: TextView = view.findViewById(R.id.tv_precio)
        val btnInscribir: Button = view.findViewById(R.id.btn_inscribir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = lista[position]

        holder.nombre.text = actividad.nombre
        holder.precio.text = "$ ${actividad.precio}"

        holder.btnInscribir.setOnClickListener {
            onInscribirClick(actividad)
        }
    }
}
