package com.shamim.frremoteattendence.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shamim.frremoteattendence.R

class CustomSpinnerAdapter(context: Context, items: List<String>) : ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false)
        val item = getItem(position)

        val textView = itemView.findViewById<TextView>(android.R.id.text1)
        textView.text = item

        return itemView
    }
}
