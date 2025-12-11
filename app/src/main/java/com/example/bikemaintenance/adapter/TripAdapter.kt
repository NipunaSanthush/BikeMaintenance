package com.example.bikemaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bikemaintenance.R
import com.example.bikemaintenance.data.TripRecord

class TripAdapter : ListAdapter<TripRecord, TripAdapter.TripViewHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateView: TextView = itemView.findViewById(R.id.tvTripDate)
        private val distView: TextView = itemView.findViewById(R.id.tvTripDistance)
        private val durView: TextView = itemView.findViewById(R.id.tvTripDuration)
        private val speedView: TextView = itemView.findViewById(R.id.tvTripSpeed)

        fun bind(record: TripRecord) {
            dateView.text = record.date
            distView.text = record.distance
            durView.text = "Duration: ${record.duration}"
            speedView.text = "Avg: ${record.avgSpeed}"
        }
    }

    class Comparator : DiffUtil.ItemCallback<TripRecord>() {
        override fun areItemsTheSame(oldItem: TripRecord, newItem: TripRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TripRecord, newItem: TripRecord): Boolean {
            return oldItem == newItem
        }
    }
}