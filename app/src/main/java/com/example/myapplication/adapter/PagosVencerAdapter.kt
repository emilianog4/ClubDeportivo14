package com.example.myapplication.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.myapplication.R

class PagosVencerAdapter(
    private val context: Context,
    private val data: ArrayList<Map<String, String>>
) : BaseAdapter() {

    override fun getCount() = data.size
    override fun getItem(position: Int) = data[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_pago_vencer, parent, false)

        val tvPeriodo = view.findViewById<TextView>(R.id.tv_periodo)
        val tvImporte = view.findViewById<TextView>(R.id.tv_importe)
        val tvDiasRest = view.findViewById<TextView>(R.id.tv_dias)

        val item = data[position]

        tvPeriodo.text = "Periodo: ${item["periodo"]}"
        tvImporte.text = "Importe: $${item["importe"]}"

        val dias = item["diasRestantes"]?.toIntOrNull() ?: 0
        tvDiasRest.text = "Días restantes: $dias"

        // Color según vencimiento
        when {
            dias <= 5 -> view.setBackgroundColor(Color.parseColor("#ffcccc"))    // rojo claro
            dias in 6..15 -> view.setBackgroundColor(Color.parseColor("#fff4cc")) // amarillo
            else -> view.setBackgroundColor(Color.parseColor("#ccffcc"))         // verde
        }

        return view
    }
}
