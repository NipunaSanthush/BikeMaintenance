package com.example.bikemaintenance

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bikemaintenance.adapter.MaintenanceAdapter
import com.example.bikemaintenance.data.MaintenanceRecord
import com.example.bikemaintenance.viewmodel.MaintenanceViewModel
import com.example.bikemaintenance.viewmodel.MaintenanceViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val maintenanceViewModel: MaintenanceViewModel by viewModels {
        MaintenanceViewModelFactory((application as BikeApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = MaintenanceAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        maintenanceViewModel.allRecords.observe(this) { records ->
            records?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Record")

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_maintenance, null)
        builder.setView(view)

        val etType = view.findViewById<EditText>(R.id.etServiceType)
        val etMileage = view.findViewById<EditText>(R.id.etMileage)
        val etCost = view.findViewById<EditText>(R.id.etCost)
        val etDate = view.findViewById<EditText>(R.id.etDate)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etDate.setText(currentDate)

        builder.setPositiveButton("Save") { _, _ ->
            val type = etType.text.toString()
            val mileageStr = etMileage.text.toString()
            val costStr = etCost.text.toString()
            val date = etDate.text.toString()

            if (type.isNotEmpty() && mileageStr.isNotEmpty() && costStr.isNotEmpty()) {
                val record = MaintenanceRecord(
                    serviceType = type,
                    mileage = mileageStr.toInt(),
                    cost = costStr.toDouble(),
                    date = date
                )
                maintenanceViewModel.insert(record)
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}