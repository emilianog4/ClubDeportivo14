package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Actividad

class ActividadAdapter(
    private val lista: List<Actividad>,
    private val onClick: (Actividad) -> Unit
) : RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = lista[position]
        holder.bind(actividad)
        holder.itemView.setOnClickListener { onClick(actividad) }
    }

    override fun getItemCount(): Int = lista.size

    class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_actividad)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tv_precio_actividad)

        fun bind(actividad: Actividad) {
            tvNombre.text = actividad.nombre
            tvPrecio.text = "$ ${actividad.precio}"
        }
    }
}
