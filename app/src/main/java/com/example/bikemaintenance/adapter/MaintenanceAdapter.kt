package com.example.bikemaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bikemaintenance.R
import com.example.bikemaintenance.data.MaintenanceRecord

class MaintenanceAdapter : ListAdapter<MaintenanceRecord, MaintenanceAdapter.MaintenanceViewHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeView: TextView = itemView.findViewById(R.id.textViewType)
        private val dateView: TextView = itemView.findViewById(R.id.textViewDate)
        private val costView: TextView = itemView.findViewById(R.id.textViewCost)
        private val mileageView: TextView = itemView.findViewById(R.id.textViewMileage)

        fun bind(record: MaintenanceRecord) {
            typeView.text = record.serviceType
            dateView.text = record.date
            costView.text = "Rs. ${record.cost}"
            mileageView.text = "${record.mileage} km"
        }
    }

    class Comparator : DiffUtil.ItemCallback<MaintenanceRecord>() {
        override fun areItemsTheSame(oldItem: MaintenanceRecord, newItem: MaintenanceRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MaintenanceRecord, newItem: MaintenanceRecord): Boolean {
            return oldItem == newItem
        }
    }
}