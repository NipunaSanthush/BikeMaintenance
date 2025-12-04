package com.example.bikemaintenance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

class HomeFragment : Fragment() {

    private val maintenanceViewModel: MaintenanceViewModel by viewModels {
        MaintenanceViewModelFactory((requireActivity().application as BikeApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = MaintenanceAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val tvTotalCost = view.findViewById<TextView>(R.id.tvTotalCost)

        maintenanceViewModel.allRecords.observe(viewLifecycleOwner) { records ->
            records?.let {
                adapter.submitList(it)

                var total = 0.0
                for (record in it) {
                    total += record.cost
                }
                tvTotalCost.text = "Rs. %.2f".format(total)
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Record")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_maintenance, null)
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
                Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}