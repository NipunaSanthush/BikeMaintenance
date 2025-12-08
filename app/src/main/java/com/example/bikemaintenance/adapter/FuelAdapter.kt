package com.example.bikemaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bikemaintenance.R
import com.example.bikemaintenance.data.FuelRecord

class FuelAdapter : ListAdapter<FuelRecord, FuelAdapter.FuelViewHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fuel, parent, false)
        return FuelViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuelViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class FuelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateView: TextView = itemView.findViewById(R.id.tvFuelDate)
        private val litersView: TextView = itemView.findViewById(R.id.tvFuelLiters)
        private val costView: TextView = itemView.findViewById(R.id.tvFuelCost)
        private val odoView: TextView = itemView.findViewById(R.id.tvFuelOdometer)

        fun bind(record: FuelRecord) {
            dateView.text = record.date
            litersView.text = "${record.liters} L"
            costView.text = "Rs. ${record.cost}"
            odoView.text = "${record.odometer} km"
        }
    }

    class Comparator : DiffUtil.ItemCallback<FuelRecord>() {
        override fun areItemsTheSame(oldItem: FuelRecord, newItem: FuelRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FuelRecord, newItem: FuelRecord): Boolean {
            return oldItem == newItem
        }
    }
}