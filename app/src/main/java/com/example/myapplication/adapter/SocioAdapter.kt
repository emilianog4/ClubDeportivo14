package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Socio

class SocioAdapter(private val socios: List<Socio>) : RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    // Esta clase interna representa la vista de un único item (item_socio.xml)
    class SocioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreApellido: TextView = itemView.findViewById(R.id.tv_item_nombre_apellido)
        val dni: TextView = itemView.findViewById(R.id.tv_item_dni)
    }

    // Este método se llama para crear un nuevo ViewHolder (una nueva fila)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        // "Inflamos" (cargamos) el layout XML del item para crear una vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_socio, parent, false)
        return SocioViewHolder(view)
    }

    // Este método se llama para rellenar los datos de una fila específica
    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        // Obtenemos el socio de la lista en la posición actual
        val socio = socios[position]
        // Asignamos los datos del socio a los TextViews del ViewHolder
        holder.nombreApellido.text = "${socio.nombre} ${socio.apellido}"
        holder.dni.text = "DNI: ${socio.dni}"
    }

    // Este método devuelve el número total de items en la lista
    override fun getItemCount(): Int {
        return socios.size
    }
}
